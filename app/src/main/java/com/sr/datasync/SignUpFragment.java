package com.sr.datasync;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpFragment extends Fragment {

    private View v;

    private Button suBtn;
    private TextInputEditText emailET, passET, rePassET;
    private TextView errTV;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mData;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_sign_up, container, false);

        suBtn = (Button) v.findViewById(R.id.su_btn);
        errTV = (TextView) v.findViewById(R.id.su_err);
        emailET = (TextInputEditText) v.findViewById(R.id.su_tf_uid);
        passET = (TextInputEditText) v.findViewById(R.id.su_tf_pass);
        rePassET = (TextInputEditText) v.findViewById(R.id.su_tf_pass_re);
        progressBar = v.findViewById(R.id.su_loadingBar);

        mData = FirebaseDatabase.getInstance();
        // mData.setPersistenceEnabled(true);

        suBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailTxt = emailET.getText().toString();
                String passwordTxt = passET.getText().toString();
                String rePassTxt = rePassET.getText().toString();
                if(emailTxt.isEmpty()){
                    errTV.setText("Email can't be empty");
                } else if(passwordTxt.isEmpty()){
                    errTV.setText("Password can't be empty");
                } else if(passwordTxt.length() < 6){
                    errTV.setText("Password must be 6 character long");
                } else if(!EmailValidator.isValidMail(emailTxt)){
                    errTV.setText("Please enter a valid email address");
                } else if(!passwordTxt.equals(rePassTxt)){
                    errTV.setText("Enter the same password");
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    emailET.setText("");
                    passET.setText("");
                    rePassET.setText("");
                    signUpUser(emailTxt, passwordTxt);
                }
            }
        });

        return v;
    }

    private void signUpUser(String email, String password){
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    EntryClass sample = new EntryClass("Sample entry title", "This is an example of a sample entry data.");
                    mData.getReference("users").child(mAuth.getCurrentUser().getUid()).setValue(email);
                    DatabaseReference ref =  mData.getReference("data").child(mAuth.getCurrentUser().getUid()).push();
                    progressBar.setVisibility(View.INVISIBLE);
                    Bundle bundleForDialog = new Bundle();
                    bundleForDialog.putString("title", "SUCCESS !");
                    bundleForDialog.putString("data", "New user signed up.");
                    StatusDialogFragment dialogFragment = new StatusDialogFragment();
                    dialogFragment.setArguments(bundleForDialog);
                    progressBar.setVisibility(View.INVISIBLE);
                    dialogFragment.show(getFragmentManager(), "DialogScreen");
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    UpdateErr(task.toString());
                }
            }
        });
    }

    private void UpdateErr(String err){
        errTV.setText(err);
    }
}