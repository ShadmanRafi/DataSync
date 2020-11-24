package com.sr.datasync;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.nfc.TagLostException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditMainActivity extends AppCompatActivity {

    private MaterialButton saveBtn, cancelBtn;
    private FloatingActionButton deleteBtn;
    private TextView titleTV, dataTV;
    private DatabaseReference myRef;
    private FirebaseUser user;
    private String dataID;
    private int position;
    private boolean saveStatus;
    private boolean existedBefore;
    private boolean deletePressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_main);
        dataID = getIntent().getStringExtra("DATA_ID");
        position = getIntent().getIntExtra("POSITION", -1);
        user = FirebaseAuth.getInstance().getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference("data").child(user.getUid());
        deletePressed = false;
        titleTV = findViewById(R.id.ep_title);
        dataTV = findViewById(R.id.ep_data);
        updateView();
        saveBtn = findViewById(R.id.save_button);
        cancelBtn = findViewById(R.id.cancel_button);
        deleteBtn = findViewById(R.id.delBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveStatus = true;
                onBackPressed();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveStatus = false;
                onBackPressed();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePressed = true;
                onBackPressed();
            }
        });
    }

    private void updateToData(){
        String titleTxt = titleTV.getText().toString();
        String dataTxt = dataTV.getText().toString();
        if((titleTxt.isEmpty() || dataTxt.isEmpty()) || (titleTV.getText().toString().equals("title") && dataTV.getText().toString().equals("data"))){
            //
        } else {
            if(existedBefore){
                App.getLocalDB().updateToTable(dataID, new EntryClass(titleTxt, dataTxt));
            } else {
                App.getLocalDB().addToTable(dataID, new EntryClass(titleTxt, dataTxt));
            }
            // updateToFirebase(titleTxt, dataTxt);
        }
    }

    private void updateToFirebase(String title, String data){
        myRef.child(dataID).setValue(new EntryClass(title, data));
    }

    private void updateView(){
        existedBefore = false;
        if(App.getLocalDB().checkIfDataExists(dataID)){
            existedBefore = true;
            EntryClass entryData = App.getLocalDB().getSingleDataFromID(dataID);
            titleTV.setText(entryData.getTitle());
            dataTV.setText(entryData.getData());
        }
    }

    private void updateViewFromFireBase(){
        myRef.child(dataID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot!=null){
                    EntryClass entry = snapshot.getValue(EntryClass.class);
                    if(entry != null){
                        titleTV.setText(entry.getTitle());
                        dataTV.setText(entry.getData());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DB ERR", error.toString());
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        //updateToData();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        updateView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateView();
    }

    @Override
    public void onBackPressed() {
        if(deletePressed){
            App.getLocalDB().deleteFromDB(dataID);
        } else if(saveStatus){
            updateToData();
        }
        super.onBackPressed();
    }
}