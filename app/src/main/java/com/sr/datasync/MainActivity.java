package com.sr.datasync;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private TextInputEditText mailET, passET;
    private Button lgBtn;
    private TextView errTV;
    private AppCompatRadioButton loginRB, signupRB;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    private boolean auth;

    private String emailTxt, passwordTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            if(!user.isAnonymous()) {
                Intent intent = new Intent(this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        } else {
            setContentView(R.layout.activity_main);

            loginRB = findViewById(R.id.lgRB);
            signupRB = findViewById(R.id.suRB);
            viewPager = findViewById(R.id.vp);

            LoginFragment loginFragment = new LoginFragment();
            SignUpFragment signUpFragment = new SignUpFragment();

            viewPagerAdapter  = new ViewPagerAdapter(getSupportFragmentManager(), 0);
            viewPagerAdapter.addFragment(loginFragment, "LOG_IN");
            viewPagerAdapter.addFragment(signUpFragment, "SIGN_UP");

            viewPager.setAdapter(viewPagerAdapter);
            viewPager.setCurrentItem(0);

            loginRB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signupRB.setChecked(false);
                    viewPager.setCurrentItem(0);
                }
            });
            signupRB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loginRB.setChecked(false);
                    viewPager.setCurrentItem(1);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        return;
    }
}