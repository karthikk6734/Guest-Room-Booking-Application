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
import com.google.android.gms.tasks.OnSuccessListener;
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
    FirebaseDatabase firebaseDatabase;
    ImageView img;
    Button bookBtn;
    DatabaseReference databaseReference;
    TextView name, address, min, max, rent, phone,status;
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_view);
        Intent i = getIntent();
        final HelperClass hlpobj = (HelperClass) i.getSerializableExtra("details");
        name = findViewById(R.id.nameDisplay);
        bookBtn = findViewById(R.id.bookNow);
        address = findViewById(R.id.addressDisplay);
        status = findViewById(R.id.statusDisplay);
        img = findViewById(R.id.photoDisplay);
        min = findViewById(R.id.minDaysDisplay);
        max = findViewById(R.id.maxDaysDisplay);
        phone = findViewById(R.id.phoneDisplay);
        rent = findViewById(R.id.rentDisplay);
        name.setText(hlpobj.getRoomName());
        address.setText(hlpobj.getAddress());
        min.setText(hlpobj.getMinBook());
        max.setText(hlpobj.getMaxBook());
        rent.setText(hlpobj.getRent());
        status.setText(hlpobj.getStatus());
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Rooms");
        final StorageReference[] storageReference = {FirebaseStorage.getInstance().getReference("Rooms")};
        FirebaseUser firebaseAuth = FirebaseAuth.getInstance().getCurrentUser();
        userId = firebaseAuth.getUid();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int flag = 0;
                for (DataSnapshot d : snapshot.getChildren()) {
                    for (DataSnapshot ds : d.getChildren()) {
                        HelperClass h = ds.getValue(HelperClass.class);
                        if (hlpobj.getRoomName().equals(h.getRoomName())) {
                            int sep = snapshot.getValue().toString().indexOf("=");
                            userId = String.valueOf(snapshot.getValue()).substring(1, sep);
                            flag = 1;
                        }
                    }
                    if (flag == 1)
                        break;
                }
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
                }else{
                    bookBtn.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        bookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i  = new Intent(getApplicationContext(),PaymentPage.class);
                i.putExtra("values",hlpobj);
                v.getContext().startActivity(i);
            }
        });
    }
}

