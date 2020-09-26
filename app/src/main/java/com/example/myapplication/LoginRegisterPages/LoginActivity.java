package com.example.myapplication.LoginRegisterPages;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.ProfilePages.OwnerPage;
import com.example.myapplication.ProfilePages.UserPage;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    /* Create Variable For Views */
    EditText email,password;
    TextView createNewAccount,forgotPassword;
    Button LogInBtn;

    FirebaseAuth firebaseAuth;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        forgotPassword = findViewById(R.id.ForgotBtn);
        email = findViewById(R.id.login_EmailAddress);
        password = findViewById(R.id.login_Password);
        LogInBtn = findViewById(R.id.login_LogInButton);
        createNewAccount = findViewById(R.id.login_SignUp);

        //Get Instance of the FirebaseAuthentication
        firebaseAuth = FirebaseAuth.getInstance();

        //Handle Sign up click
        createNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class)); //Go to the Register Page
            }
        });

        //Handle Forgotpassword Event
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText et = new EditText(v.getContext());

                //Create AlertDialog Box to Get Password
                final AlertDialog.Builder resetDialog = new AlertDialog.Builder(v.getContext());
                resetDialog.setTitle("Reset Password?").setMessage("Enter Your Email").setView(et);

                //Handle the Yes Button
                resetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email = et.getText().toString().trim();

                        //Send ResetPassword Link to the User Mail id
                        firebaseAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(LoginActivity.this,"Reset Mail Has Sent",Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginActivity.this,"Check Your Email:"+e.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
                resetDialog.create().show();
            }
        });

        //Handle Log in Button Click
        LogInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get the email and password from user entered details
                String emailText = email.getText().toString().trim();
                String passWordText = password.getText().toString().trim();

                if(TextUtils.isEmpty(emailText)){
                    email.setError("Please Enter Email"); //Email not entered Set Error
                    return;
                }if(TextUtils.isEmpty(passWordText)){
                    password.setError("Please Enter Password");  //Password not entered Set Error
                    return;
                }

                //Check with FireBase If they have account with this email
                firebaseAuth.signInWithEmailAndPassword(emailText,passWordText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {

                            //Get Unique Current User Id
                            String id = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            //Get reference for the Node User ID in Database
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Owner").child(id);

                            databaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        startActivity(new Intent(getApplicationContext(), OwnerPage.class)); //If user present in the owner node -> Go to the owner's profile
                                    }else{
                                        startActivity(new Intent(getApplicationContext(), UserPage.class)); //Else go to the Tenant's Profile
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }else{
                            Toast.makeText(LoginActivity.this,"Check Email and Password",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    //Handle Back Button Click
    @Override
    public void onBackPressed() {
        return;
    }
}