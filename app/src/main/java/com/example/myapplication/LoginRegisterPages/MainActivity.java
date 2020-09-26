package com.example.myapplication.LoginRegisterPages;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity {
    EditText email,password;
    TextView CreateBtn,ForgetBtn;
    FirebaseAuth firebaseAuth;
    Button LogInBtn;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        ForgetBtn = findViewById(R.id.ForgotBtn);
        email = findViewById(R.id.EmailAddress);
        password = findViewById(R.id.Password);
        LogInBtn = findViewById(R.id.LogInButton);
        CreateBtn = findViewById(R.id.SignUp);
        CreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });
        ForgetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText et = new EditText(v.getContext());
                final AlertDialog.Builder resetDialog = new AlertDialog.Builder(v.getContext());
                resetDialog.setTitle("Reset Password?").setMessage("Enter Your Email").setView(et);
                resetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email = et.getText().toString().trim();
                        firebaseAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this,"Reset Mail Has Sent",Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this,"Check Your Email:"+e.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
                resetDialog.create().show();
            }
        });
        LogInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(email.getText().toString().trim())){
                    email.setError("Please Enter Email");
                    return;
                }if(TextUtils.isEmpty(password.getText().toString().trim())){
                    password.setError("Please Enter Password");
                    return;
                }
                firebaseAuth.signInWithEmailAndPassword(email.getText().toString().trim(),password.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Owner").child(id);
                            databaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        startActivity(new Intent(getApplicationContext(), OwnerPage.class));
                                    }else{
                                        startActivity(new Intent(getApplicationContext(), UserPage.class));
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }else{
                            Toast.makeText(MainActivity.this,"Check Email and Password",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    static  int backpress=0;
    @Override
    public void onBackPressed() {
        return;
    }
}