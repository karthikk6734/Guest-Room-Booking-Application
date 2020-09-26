package com.example.myapplication.LoginRegisterPages;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup;

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
    EditText Name,Email,Password,ConfirmPassword,Phone;
    Button SignUpBtn;
    TextView LogInBtn;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase db;
    DatabaseReference reference;
    RadioGroup radioGroup;
    RadioButton Tenant;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Name = findViewById(R.id.name);
        Email = findViewById(R.id.email);
        LogInBtn = findViewById(R.id.signin);
        SignUpBtn = findViewById(R.id.signup);
        Password = findViewById(R.id.pswd);
        ConfirmPassword = findViewById(R.id.cnfm_pswd);
        Phone = findViewById(R.id.ph);
        radioGroup = findViewById(R.id.ownerortenent);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        Tenant = findViewById(radioGroup.getCheckedRadioButtonId());
        reference = db.getReference(Tenant.getText().toString());
        LogInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Tenant = findViewById(radioGroup.getCheckedRadioButtonId());
                reference = db.getReference(Tenant.getText().toString());
            }
        });
        SignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(Email.getText().toString().trim())) {
                    Email.setError("Please Enter Email!");
                    return;
                }
                if(TextUtils.isEmpty(Password.getText().toString().trim())){
                    Password.setError("Please Enter Password!");
                    return;
                }
                if(TextUtils.isEmpty(Name.getText().toString().trim())) {
                    Name.setError("Please Enter Name!");
                    return;
                }
                if(!Password.getText().toString().equals(ConfirmPassword.getText().toString())) {
                    ConfirmPassword.setError("Password and Confirm Password not Same");
                    return;
                }
                firebaseAuth.createUserWithEmailAndPassword(Email.getText().toString().trim(), Password.getText().toString().trim())
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(RegisterActivity.this,"Verify Your Email and Login",Toast.LENGTH_LONG).show();
                                            String userId = firebaseAuth.getCurrentUser().getUid();
                                            String name = Name.getText().toString();
                                            String email = Email.getText().toString();
                                            String password = Password.getText().toString();
                                            String phone = Phone.getText().toString();
                                            HelperClass hc = new HelperClass(name,email,password,phone);
                                            reference.child(userId).setValue(hc);
                                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
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
    public void onBackPressed() {
        return;
    }
}