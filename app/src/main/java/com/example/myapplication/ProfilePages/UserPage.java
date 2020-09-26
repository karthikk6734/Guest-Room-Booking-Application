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
import com.example.myapplication.R;
import com.example.myapplication.SearchRooms;
import com.example.myapplication.ViewBookedRooms;
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

public class UserPage extends AppCompatActivity {
    //Create Variable for the views
    ImageView img;
    TextView name,email,phone;
    StorageReference storageReference;
    Button search,viewRooms,logout;
    int TAKE_IMAGE_CODE=1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);
        //Initialize the Views
        img = findViewById(R.id.user_ProfileImg);
        name = findViewById(R.id.user_Name);
        email = findViewById(R.id.user_Email);
        phone = findViewById(R.id.user_Phone);
        search = findViewById(R.id.user_SearchRooms);
        viewRooms = findViewById(R.id.user_BookedRooms);
        logout = findViewById(R.id.user_LogOut);

        //Handle the viewRooms button Click
        viewRooms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ViewBookedRooms.class)); //Start ViewBookedRooms Activity
            }
        });

        //Handle Search Button Click
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SearchRooms.class)); //Start SearchRooms Activity
            }
        });

        //Handle LogOut button Click
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class)); //Start LogIn Activity
            }
        });


        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();  //Get the Current User

        if(firebaseUser!=null) {
            String id = FirebaseAuth.getInstance().getCurrentUser().getUid(); //Get ID of Current User
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Tenant").child(id); //Go to the Current User Reference in Database
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //Get Details as a HelperClass Object
                    HelperClass hc = snapshot.getValue(HelperClass.class);

                    //Set the User Details
                    name.setText(hc.getName());
                    email.setText(hc.getEmail());
                    phone.setText(hc.getPhone());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            if(firebaseUser.getPhotoUrl()!=null) {
                //If Photo exists Download the Photo and set as Profile Image
                Glide.with(this).load(firebaseUser.getPhotoUrl()).into(img);
            }
        }

        //Change the Profile image
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleClick(v);
            }
        });
    }
    private void handleClick(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //Open the Camera
        if(intent.resolveActivity(getPackageManager())!=null) {
            startActivityForResult(intent,TAKE_IMAGE_CODE); //If picture taken
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==TAKE_IMAGE_CODE){
            if(resultCode==RESULT_OK){
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");  //Get Image as Bitmap
                img.setImageBitmap(bitmap); //setToProfileImage
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
            public void onSuccess(Uri uri) {
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser(); //get Current User
                UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();
                //To Update user Profile Information
                firebaseUser.updateProfile(request).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UserPage.this,"Profile Picture Updated",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UserPage.this,"Profile Not Updated",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    //Handle BackButton CLick
    @Override
    public void onBackPressed() {
        return;
    }
}