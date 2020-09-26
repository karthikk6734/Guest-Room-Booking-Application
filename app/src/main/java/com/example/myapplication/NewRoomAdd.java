package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.example.myapplication.HelperClasses.HelperClass;
import com.example.myapplication.ProfilePages.OwnerPage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class NewRoomAdd extends AppCompatActivity {
    EditText name,address,city,minimum,maximum,rent;
    String url;
    Button addBtn,addPhoto;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    String status = "UnBooked";
    Uri profileUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_room_add);
        addBtn = findViewById(R.id.addRoom);
        firebaseAuth = FirebaseAuth.getInstance();
        name = findViewById(R.id.roomName);
        address = findViewById(R.id.addressLine1);
        city = findViewById(R.id.city);
        minimum = findViewById(R.id.minimumBook);
        maximum = findViewById(R.id.maximumBook);
        rent = findViewById(R.id.rent);
        addPhoto = findViewById(R.id.addPhoto);
        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleClick(v);
            }
        });
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseDatabase = FirebaseDatabase.getInstance();
                reference = firebaseDatabase.getReference("Rooms");
                String userId = firebaseAuth.getCurrentUser().getUid();
                HelperClass hc = new HelperClass(name.getText().toString(), address.getText().toString(), city.getText().toString().toLowerCase(), minimum.getText().toString(), maximum.getText().toString(), rent.getText().toString(),status);
                reference.child(userId).child(name.getText().toString()).setValue(hc);
                startActivity(new Intent(getApplicationContext(), OwnerPage.class));
            }
        });
    }
    private void handleClick(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager())!=null) {
            startActivityForResult(intent,1000);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1000){
            if(resultCode==RESULT_OK){
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                handleUpload(bitmap);
            }
        }
    }

    private void handleUpload(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference().child("Rooms").child(uid).child(name.getText().toString()).child(uid+".jpeg");
        storageReference.putBytes(baos.toByteArray());
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String userId = firebaseAuth.getCurrentUser().getUid();
                HelperClass hc = new HelperClass(name.getText().toString(), address.getText().toString(), city.getText().toString().toLowerCase(), minimum.getText().toString(), maximum.getText().toString(), rent.getText().toString(),status,String.valueOf(uri));
                reference.child(userId).child(name.getText().toString()).setValue(hc);
                startActivity(new Intent(getApplicationContext(), OwnerPage.class));
            }
        });
    }
}