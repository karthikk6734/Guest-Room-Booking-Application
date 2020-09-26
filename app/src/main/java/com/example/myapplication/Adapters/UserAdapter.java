package com.example.myapplication.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.DetailsView;
import com.example.myapplication.HelperClasses.HelperClass;
import com.example.myapplication.R;
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
import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {

    Context context;
    ArrayList<HelperClass> helper;
    String userId;
    public UserAdapter(Context c,ArrayList<HelperClass> h){
        context = c;
        helper = h;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row, null));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final HelperClass obj = helper.get(position);
        holder.roomname.setText(obj.getRoomName());
        holder.address.setText(obj.getAddress());
        holder.rentday.setText(obj.getRent());
        holder.hlp = obj;
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("Rooms");
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
                        if (obj.getRoomName().equals(h.getRoomName())) {
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
                        storageReference[0] = storageReference[0].child(userId).child(obj.getRoomName()).child(userId + ".jpeg");
                        //Log.d("loc", String.valueOf(storageReference[0]));
                        storageReference[0].getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                holder.img.setImageBitmap(bitmap);
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
    }

    @Override
    public int getItemCount() {
        return helper.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView roomname,address,rentday;
        ImageView img;
        HelperClass hlp;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.CardImg);
            roomname =  itemView.findViewById(R.id.nameofRoom);
            address =  itemView.findViewById(R.id.AddressOfRoom);
            rentday = itemView.findViewById(R.id.rentDay);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(), DetailsView.class);
                    i.putExtra("details", hlp);
                    v.getContext().startActivity(i);
                }
            });
        }
    }
}
