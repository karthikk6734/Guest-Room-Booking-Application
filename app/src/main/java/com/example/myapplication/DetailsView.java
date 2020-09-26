package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.HelperClasses.HelperClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
public class DetailsView extends AppCompatActivity {
    //Create Variables for Views
    ImageView img;
    Button bookBtn,deleteBtn;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    TextView name, address, min, max, rent, phone,status;
    String userId;
    HelperClass hlpobj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_view);

        //Get Details from the Card View
        Intent i = getIntent();
        hlpobj = (HelperClass) i.getSerializableExtra("details");

        //Initialize the Views
        name = findViewById(R.id.details_RoomName);
        bookBtn = findViewById(R.id.details_BookNow);
        address = findViewById(R.id.details_Address);
        status = findViewById(R.id.details_status);
        img = findViewById(R.id.photoDisplay);
        min = findViewById(R.id.details_MinDays);
        max = findViewById(R.id.details_maxDays);
        phone = findViewById(R.id.details_Phone);
        rent = findViewById(R.id.details_rent);
        deleteBtn = findViewById(R.id.details_deleteRoom);

        //Get Details from HelperClass Object
        name.setText(hlpobj.getRoomName());
        address.setText(hlpobj.getAddress());
        min.setText(hlpobj.getMinBook());
        max.setText(hlpobj.getMaxBook());
        rent.setText(hlpobj.getRent());
        status.setText(hlpobj.getStatus());

        firebaseDatabase = FirebaseDatabase.getInstance(); //Get Current Database Instance
        databaseReference = firebaseDatabase.getReference().child("Rooms"); //get Rooms Reference
        final StorageReference[] storageReference = {FirebaseStorage.getInstance().getReference("Rooms")}; //Get Firebase Storage Reference
        FirebaseUser firebaseAuth = FirebaseAuth.getInstance().getCurrentUser();  //Get Current Usee
        userId = firebaseAuth.getUid();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int flag = 0;
                //Get the owners Unique UserID
                for (DataSnapshot d : snapshot.getChildren()) {
                    for (DataSnapshot ds : d.getChildren()) {
                        HelperClass h = ds.getValue(HelperClass.class);
                        if (hlpobj.getRoomName().equals(h.getRoomName())) {
                            Log.d("val", String.valueOf(d.getValue()));
                            int sep = snapshot.getValue().toString().indexOf("=");
                            userId = String.valueOf(snapshot.getValue()).substring(1, sep);
                            flag = 1;
                        }
                    }
                    if (flag == 1)
                        break;
                }

                //Get the owner phone number and Room images from the Database Storage
                firebaseDatabase.getReference("Owner").child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        HelperClass h = snapshot.getValue(HelperClass.class);
                        phone.setText(h.getPhone());
                        storageReference[0] = storageReference[0].child(userId).child(hlpobj.getRoomName()).child(userId + ".jpeg");
                        Log.d("loc", String.valueOf(storageReference[0]));
                        storageReference[0].getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                img.setImageBitmap(bitmap);
                            }
                        });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        databaseReference = FirebaseDatabase.getInstance().getReference("Tenant").child(userId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    deleteBtn.setVisibility(View.INVISIBLE);
                    bookBtn.setVisibility(View.VISIBLE);
                }else{
                    deleteBtn.setVisibility(View.VISIBLE);
                    bookBtn.setVisibility(View.GONE); //If it's owner Book Button Disabled
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Pass the Current Room Details to the Payment Page
        bookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i  = new Intent(getApplicationContext(),PaymentPage.class);
                i.putExtra("values",hlpobj);
                v.getContext().startActivity(i);
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("hel","Clicked");
                databaseReference.child("Rooms").child(userId).child(hlpobj.getRoomName()).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("hel","removed");
                    }
                });
                startActivity(new Intent(getApplicationContext(),OwnerDisplayRooms.class));
            }
        });
    }
}

