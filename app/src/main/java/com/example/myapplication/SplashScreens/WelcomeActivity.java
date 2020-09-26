package com.example.myapplication.SplashScreens;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.myapplication.LoginRegisterPages.MainActivity;
import com.example.myapplication.LoginRegisterPages.RegisterActivity;
import com.example.myapplication.R;

public class WelcomeActivity extends AppCompatActivity {

    Button signupbtn,loginbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        signupbtn = findViewById(R.id.welcome_signup_btn);
        loginbtn = findViewById(R.id.welcome_login_btn);
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });
    }
}