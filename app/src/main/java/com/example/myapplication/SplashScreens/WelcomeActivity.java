package com.example.myapplication.SplashScreens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.LoginRegisterPages.LoginActivity;
import com.example.myapplication.LoginRegisterPages.RegisterActivity;
import com.example.myapplication.R;

public class WelcomeActivity extends AppCompatActivity {
    //Create Variable for Button View
    Button signupbtn,loginbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //Initialize Buttons
        signupbtn = findViewById(R.id.welcome_signup_btn);
        loginbtn = findViewById(R.id.welcome_signin_btn);

        //Handle Login Button click
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class)); //Intent to start LoginActivity
            }
        });

        //Handle SignUp Button click
        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class)); //Intent to start RegsiterActivity
            }
        });
    }
}