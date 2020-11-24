package com.sr.datasync;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class HomeActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseDatabase mData;
    private DatabaseReference ref;
    private TreeMap<String, EntryClass> firebaseData;

    private MaterialButton logoutBtn, syncButton;
    private FloatingActionButton addBtn;
    private RecyclerView recyclerView;
    private ItemAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
    private ProgressBar progressBar;

    private ArrayList<String> dataIDList = new ArrayList<>();
    private ArrayList<EntryClass> entryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance();
        ref = mData.getReference("data").child(mAuth.getCurrentUser().getUid());
        loadFromLocal();

        addBtn = findViewById(R.id.addBtn);
        syncButton = findViewById(R.id.upBtn);
        logoutBtn = findViewById(R.id.logoutBtn);
        recyclerView = findViewById(R.id.itemsRV);
        progressBar = findViewById(R.id.loadingBar);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference dbRef = ref.push();
                Intent intent = new Intent(getApplicationContext(), EditMainActivity.class);
                intent.putExtra("DATA_ID", dbRef.getKey());
                startActivity(intent);
            }
        });

        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncToDatabase();
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
                startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startIntent);
            }
        });
    }

    private void loadDataFromFirebase(){
        Log.d("HOME","loadFromFireBase");
        ArrayList<String> newDataIDs = new ArrayList<>();
        ArrayList<EntryClass> newEntries = new ArrayList<>();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot != null){
                    for(DataSnapshot d: snapshot.getChildren()){
                        String key = d.getKey();
                        EntryClass entry = (EntryClass) d.getValue(EntryClass.class);
                        newDataIDs.add(key);
                        newEntries.add(entry);
                    }
                    Log.d("HOME","loadfromfirebase/addingToLOCAL");
                    App.getLocalDB().addMultipleToTable(newDataIDs, newEntries);
                    updateRecyclerView(newEntries, newDataIDs);
                    checkLocal();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FIREBASE DATA", error.toString());
            }
        });
    }

    @Override
    protected void onRestart() {
        Log.d("HOME","RESTART");
        super.onRestart();
        loadFromLocal();
        // loadDataFromFirebase();
    }


    // populate view with class data
    private void populateRecyclerView(){
        Log.d("HOME","populateRECYCLER");
        mAdapter = new ItemAdapter(entryList, dataIDList);
        if(recyclerView==null)
            recyclerView = findViewById(R.id.itemsRV);
        if(layoutManager==null)
            layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
    }

    // update view with new data
    private void updateRecyclerView(ArrayList<EntryClass> entries, @NonNull ArrayList<String> ids){
        Log.d("HOME","updateRECYCLER");
        boolean isEmpty = dataIDList.size() < 1;
        dataIDList = ids;
        entryList = entries;
        if(isEmpty){
            populateRecyclerView();
        } else {
            mAdapter.updateView(entryList, dataIDList);
        }
    }

    // upload local data to firebase if changed
    private void syncToDatabase(){
        Log.i("ONCLICK", "SYNC BTN PRESSED");
        startProgressbar();
        // 2nd option implemented
        /*
        * data can be updated to cloud 2 way
        * 1 - one by one
        *       but it will create problem if any entry is deleted
        * 2 - replace the whole node
        *       but it will take more time, and data, I think
        *       it will be used even if nothing new is added
        * 2nd option is suitable for updating after offline session
        * */
        int localDataSize = dataIDList.size();
        Bundle bundle = new Bundle();
        firebaseData = new TreeMap<>();
        for(int i=0; i<localDataSize; i++){
            String firebaseKey = dataIDList.get(i);
            firebaseData.put(firebaseKey, entryList.get(i));
        }
        Log.d("SYNC", Integer.toString(localDataSize));
        if(isNetworkConnected()){
            Log.d("SYNC", "internet connected");
            ref.setValue(firebaseData).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d("SYNC", "setvalue success");
                    bundle.putString("title", "SUCCESS !");
                    bundle.putString("data", "Offline data uploaded to firebase successfully.");
                    showDialog(bundle);
                }
            })
            .addOnCanceledListener(new OnCanceledListener() {
                @Override
                public void onCanceled() {
                    Log.d("SYNC", "setvalue cancelled");
                    bundle.putString("title", "SYNC CANCELLED");
                    bundle.putString("data", "Offline data uploaded cancelled.");
                    showDialog(bundle);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("SYNC", "setvalue failed");
                    bundle.putString("title", "SYNC FAILED");
                    bundle.putString("data", "Offline data upload failed. "+e.toString());
                    showDialog(bundle);
                }
            });
        } else {
            Log.d("SYNC", "no internet");
            bundle.putString("title", "NO INTERNET");
            bundle.putString("data", "You have no internet access. Please try again after connecting to internet.");
            showDialog(bundle);
        }
    }

    // loading data from local database
    private void loadFromLocal(){
        Log.d("HOME", "Loadfromlocal");
        List<ArrayList> doubleList = App.getLocalDB().getAllData();
        ArrayList<String> idList = doubleList.get(0);
        ArrayList<EntryClass> entryClassList = doubleList.get(1);
        if(idList.size() == 0){
            Log.d("HOME", "to loadfromlocal/firebase");
            // if local database is empty, try to load DATA from firebase
            loadDataFromFirebase();
        } else {
            Log.d("HOME", "loadfromlocal/local");
            dataIDList = idList;
            entryList = entryClassList;
            populateRecyclerView();
        }
    }

    // internet connection check
    private boolean isNetworkConnected(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    // debug function for local database
    private void checkLocal(){
        List<ArrayList> doubleList = App.getLocalDB().getAllData();
        ArrayList<String> idList = doubleList.get(0);
        ArrayList<EntryClass> entryClassList = doubleList.get(1);
        for(int j=0; j<idList.size(); j++){
            Log.d("LOCAL_DATABASE_ENTRIES", "DATA="+entryClassList.get(j).getData());
        }
    }

    private void showDialog(Bundle bundleForDialog){
        recyclerView.setClickable(true);
        firebaseData.clear();
        StatusDialogFragment dialogFragment = new StatusDialogFragment();
        dialogFragment.setArguments(bundleForDialog);
        progressBar.setVisibility(View.INVISIBLE);
        dialogFragment.show(getSupportFragmentManager(), "DialogScreen");
    }

    private void startProgressbar(){
        progressBar.setVisibility(View.VISIBLE);
    }
}