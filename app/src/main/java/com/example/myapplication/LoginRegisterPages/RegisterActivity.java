package com.example.myapplication.LoginRegisterPages;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.HelperClasses.HelperClass;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity{
    //Create Variable for Views
    EditText Name,Email,Password,ConfirmPassword,Phone;
    Button SignUpBtn;
    TextView LogInBtn;
    RadioGroup radioGroup;
    RadioButton Tenant;

    //Create Variables for Firebase
    FirebaseAuth firebaseAuth;
    FirebaseDatabase db;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Initialize Views
        Name = findViewById(R.id.signUp_name);
        Email = findViewById(R.id.signUp_Email);
        LogInBtn = findViewById(R.id.signUp_signIn);
        SignUpBtn = findViewById(R.id.signUp_SignInBtn);
        Password = findViewById(R.id.signUp_Password);
        ConfirmPassword = findViewById(R.id.signUp_cnfm_pswd);
        Phone = findViewById(R.id.signUp_Phone);
        radioGroup = findViewById(R.id.owner_or_Tenant);
        Tenant = findViewById(radioGroup.getCheckedRadioButtonId());

        //Get Instance of firebase Database and Authentication
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        reference = db.getReference(Tenant.getText().toString());

        //Handle the Login Button Activity
        LogInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });

        //Handle Radio Button Change Activity
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Tenant = findViewById(radioGroup.getCheckedRadioButtonId());
                reference = db.getReference(Tenant.getText().toString());
            }
        });

        //Handle SignUp Button Activity
        SignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(Email.getText().toString().trim())) {
                    Email.setError("Please Enter Email!"); //If email not entered setError
                    return;
                }
                if(TextUtils.isEmpty(Password.getText().toString().trim())){
                    Password.setError("Please Enter Password!"); //If password not entered setError
                    return;
                }
                if(TextUtils.isEmpty(Name.getText().toString().trim())) {
                    Name.setError("Please Enter Name!"); //If Name not entered setError
                    return;
                }
                if(!Password.getText().toString().equals(ConfirmPassword.getText().toString())) {
                    ConfirmPassword.setError("Password and Confirm Password not Same"); //If password and Confirm Password not equal setError
                    return;
                }

                //Create New Account with the Email and Password Entered
                firebaseAuth.createUserWithEmailAndPassword(Email.getText().toString().trim(), Password.getText().toString().trim())
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = firebaseAuth.getCurrentUser();

                                    //Send Verification Link to the Email
                                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(RegisterActivity.this,"Verify Your Email and Login",Toast.LENGTH_LONG).show();
                                            String userId = firebaseAuth.getCurrentUser().getUid();
                                            String name = Name.getText().toString();
                                            String email = Email.getText().toString();
                                            String password = Password.getText().toString();
                                            String phone = Phone.getText().toString();
                                            HelperClass hc = new HelperClass(name,email,password,phone); //Pass the values to the HelperClass
                                            reference.child(userId).setValue(hc);     //Pass the HelperClass object to set the values in Database
                                            startActivity(new Intent(getApplicationContext(), LoginActivity.class)); //Start the LoginActivity
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(RegisterActivity.this,"Please Check Your MailId",Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }else{
                                    Toast.makeText(RegisterActivity.this,"Account already exists Try To login",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    @Override
    //Handle Back Button Press
    public void onBackPressed() {
        return;
    }
}