package com.sr.datasync;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment {

    private Button liBtn;
    private TextInputEditText emailET, passET;
    private TextView errTV;
    private View v;
    private ProgressBar progressBar;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_login, container, false);

        liBtn = (Button) v.findViewById(R.id.li_btn);
        errTV = (TextView) v.findViewById(R.id.li_err);
        emailET = (TextInputEditText) v.findViewById(R.id.li_tf_uid);
        passET = (TextInputEditText) v.findViewById(R.id.li_tf_pass);
        progressBar = v.findViewById(R.id.li_loadingBar);

        liBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errTV.setText("");
                String emailTxt = emailET.getText().toString();
                String passwordTxt = passET.getText().toString();
                if(emailTxt.isEmpty()){
                    errTV.setText("Email can't be empty !");
                } else if(passwordTxt.isEmpty()){
                    errTV.setText("Password can't be empty !");
                } else if(passwordTxt.length() < 6){
                    errTV.setText("Password must be 6 character long !");
                } else if(!EmailValidator.isValidMail(emailTxt)){
                    errTV.setText("Please enter a valid email address");
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    emailET.setText("");
                    passET.setText("");
                    loginUser(emailTxt, passwordTxt);
                }

            }
        });

        return v;
    }

    private void loginUser(String email, String password){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                    // to home page
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("USER", user);
                    progressBar.setVisibility(View.INVISIBLE);
                    startActivity(intent);
                    App.getLocalDB().resetAllData();

                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    String err = String.valueOf(task.getException());
                    Log.e("FAIL", String.valueOf(task.getException()));
                    UpdateErr(err);
                }
            }
        });
    }

    void UpdateErr(String err){
        errTV.setText(err);
    }
}