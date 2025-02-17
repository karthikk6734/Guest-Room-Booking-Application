package com.example.myapplication.ProfilePages;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.HelperClasses.HelperClass;
import com.example.myapplication.LoginRegisterPages.LoginActivity;
import com.example.myapplication.NewRoomAdd;
import com.example.myapplication.OwnerDisplayRooms;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class OwnerPage extends AppCompatActivity {
    //Create Variables for the Views
    ImageView img;
    TextView name,email,phone;
    Button goToRooms,add,logout;
    StorageReference storageReference;
    int TAKE_IMAGE_CODE=1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_page);
        //Initialize Views
        img = findViewById(R.id.owner_ProfileImage);
        name = findViewById(R.id.owner_Name);
        email = findViewById(R.id.owner_Email);
        phone = findViewById(R.id.owner_Phone);
        goToRooms = findViewById(R.id.owner_Rooms);
        add = findViewById(R.id.owner_AddRooms);
        logout = findViewById(R.id.owner_LogOut);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser(); //get The Curret User

        //Handle ViewRooms Button Click
        goToRooms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), OwnerDisplayRooms.class)); //Start Display Rooms Activity
            }
        });

        //Handle Add Rooms Button Click
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NewRoomAdd.class)); //Start New Room Add Activity
            }
        });

        //Handle Log Out Button CLick
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class)); //Start Login Activity
            }
        });

        //If User Exists
        if(firebaseUser!=null) {
            String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Owner").child(id);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    HelperClass hc = snapshot.getValue(HelperClass.class); //Get User Details as HelperClass Object
                    name.setText(hc.getName());
                    email.setText(hc.getEmail());
                    phone.setText(hc.getPhone());
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            if(firebaseUser.getPhotoUrl()!=null) {
                //Set the Profile Img
                Glide.with(this).load(firebaseUser.getPhotoUrl()).into(img);
            }
        }
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleClick(v);
            }
        });
    }
    private void handleClick(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager())!=null) {
            startActivityForResult(intent,TAKE_IMAGE_CODE);
        }
    }

    //Handle Back Click Activity
    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==TAKE_IMAGE_CODE){
            if(resultCode==RESULT_OK){
                Bitmap bitmap = (Bitmap) data.getExtras().get("data"); //Get Image as Bitmap
                img.setImageBitmap(bitmap); //Set Image to Profile
                handleUpload(bitmap);
            }
        }
    }

    //Upload Image to the Firebase Storage
    private void handleUpload(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference().child("Profile Images").child(uid+".jpeg"); //Get Reference for Storage
        storageReference.putBytes(baos.toByteArray()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                getDownloadUrl(storageReference);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void getDownloadUrl(StorageReference storageReference) {
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri){
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser(); //get Current User
                UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();
                firebaseUser.updateProfile(request).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(OwnerPage.this,"Profile Picture Updated",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(OwnerPage.this,"Profile Not Updated",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

}