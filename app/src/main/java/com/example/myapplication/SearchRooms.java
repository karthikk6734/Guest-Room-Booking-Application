package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adapters.UserAdapter;
import com.example.myapplication.HelperClasses.HelperClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchRooms extends AppCompatActivity {
    ArrayList<HelperClass> list;
    RecyclerView recyclerView;
    EditText cityname;
    ImageButton buttonSearch;
    UserAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_rooms);
        buttonSearch = findViewById(R.id.searchButton);
        recyclerView = findViewById(R.id.userRecView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        cityname = findViewById(R.id.searchText);
        list = new ArrayList<>();
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Rooms");
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            list.clear();
                            for(DataSnapshot d:snapshot.getChildren()) {
                                for(DataSnapshot ds:d.getChildren()) {
                                    HelperClass h = ds.getValue(HelperClass.class);
                                    if (h.getCity().startsWith(cityname.getText().toString().toLowerCase())&&h.getStatus().equals("UnBooked"))
                                        list.add(h);
                                }
                            }
                            adapter = new UserAdapter(getApplicationContext(),list);
                            recyclerView.setAdapter(adapter);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

    }
}