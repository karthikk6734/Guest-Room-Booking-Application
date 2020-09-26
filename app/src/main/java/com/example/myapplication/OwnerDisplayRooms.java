package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import android.widget.TextView;

import com.example.myapplication.Adapters.MyAdapter;
import com.example.myapplication.HelperClasses.HelperClass;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OwnerDisplayRooms extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    TextView toptext;
    ArrayList<HelperClass> list;
    RecyclerView recyclerView;
    public static RecyclerView.Adapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_display_rooms);
        recyclerView = findViewById(R.id.recview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        list = new ArrayList<>();
        toptext = findViewById(R.id.roomstats);
        firebaseAuth = FirebaseAuth.getInstance();
        String id = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Rooms").child(id);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    toptext.setText("Your Rooms");
                    for(DataSnapshot ds:snapshot.getChildren()) {
                        HelperClass h = ds.getValue(HelperClass.class);
                        list.add(h);
                    }
                    adapter = new MyAdapter(getApplicationContext(),list);
                    recyclerView.setAdapter(adapter);
                }else{
                    toptext.setText("No Rooms Available");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}