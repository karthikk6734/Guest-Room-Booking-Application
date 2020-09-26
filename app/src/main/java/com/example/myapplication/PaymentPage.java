package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.HelperClasses.HelperClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PaymentPage extends AppCompatActivity {
    TextView amount,day;
    EditText name,phoneNumber;
    Button plus,minus,submitBtn;
    int m,minStay,maxStay;
    private int UPI_PAYMENT=1;
    DatePicker datePicker;
    HelperClass hlpobj;
    String date;
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_page);
        name = findViewById(R.id.customerName);
        phoneNumber = findViewById(R.id.phoneNumber);
        day = findViewById(R.id.dayCounter);
        datePicker = findViewById(R.id.datePicker);
        plus = findViewById(R.id.plusButton);
        minus = findViewById(R.id.minusButton);
        amount = findViewById(R.id.amount);
        submitBtn = findViewById(R.id.paymentButton);
        date = datePicker.getDayOfMonth() + 1 + "-" + datePicker.getMonth() + 1 + "-" + datePicker.getYear();
        Intent i = getIntent();
        hlpobj = (HelperClass) i.getSerializableExtra("values");
        minStay = Integer.parseInt(hlpobj.getMinBook().trim());
        m = minStay+1;
        maxStay = Integer.parseInt(hlpobj.getMaxBook().trim());
        amount.setText(String.valueOf(Integer.parseInt(hlpobj.getMinBook())*Integer.parseInt(hlpobj.getRent())));
        datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Log.d("date",datePicker.getDayOfMonth()+" "+datePicker.getMonth()+" "+datePicker.getYear());

            }
        });
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(minStay<maxStay){
                    minStay++;
                    day.setText(String.valueOf(minStay));
                    amount.setText(String.valueOf(Integer.parseInt(hlpobj.getRent())*minStay));
                }
            }
        });
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(minStay>m){
                    minStay--;
                    day.setText(String.valueOf(minStay));
                    amount.setText(String.valueOf(Integer.parseInt(hlpobj.getRent())*minStay));
                }
            }
        });
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payUsingUpi(amount.getText().toString(),name.getText().toString(),hlpobj.getRoomName());
            }
        });

    }
    void payUsingUpi(String amount, String name, String note) {

        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", "9688228266@paytm")
                .appendQueryParameter("pn", name)
                .appendQueryParameter("tn",note )
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                .build();


        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);

        // will always show a dialog to user to choose an app
        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");

        // check if intent resolves
        if(null != chooser.resolveActivity(getPackageManager())) {
            startActivityForResult(chooser, UPI_PAYMENT);
        } else {
            Toast.makeText(getApplicationContext(),"No UPI app found, please install one to continue",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UPI_PAYMENT) {
            if ((RESULT_OK == resultCode) || (resultCode == 11)) {
                if (data != null) {
                    String trxt = data.getStringExtra("response");
                    Log.d("UPI", "onActivityResult: " + trxt);
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add(trxt);
                    upiPaymentDataOperation(dataList);
                } else {
                    Log.d("UPI", "onActivityResult: " + "Return data is null");
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
            } else {
                Log.d("UPI", "onActivityResult: " + "Return data is null"); //when user simply back without payment
                ArrayList<String> dataList = new ArrayList<>();
                dataList.add("nothing");
                upiPaymentDataOperation(dataList);
            }
        } else {
            throw new IllegalStateException("Unexpected value: " + requestCode);
        }
    }

    private void upiPaymentDataOperation(final ArrayList<String> data) {
        if (isConnectionAvailable(PaymentPage.this)) {
            String str = data.get(0);
            Log.d("UPIPAY", "upiPaymentDataOperation: "+str);
            String paymentCancel = "";
            if(str == null) str = "discard";
            String status = "";
            String approvalRefNo = "";
            String response[] = str.split("&");
            for (int i = 0; i < response.length; i++) {
                String equalStr[] = response[i].split("=");
                if(equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    }
                    else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                        approvalRefNo = equalStr[1];
                    }
                }
                else {
                    paymentCancel = "Payment cancelled by user.";
                }
            }

            if (status.equals("success")) {
                Toast.makeText(PaymentPage.this, "Transaction successful.", Toast.LENGTH_SHORT).show();
            }
            else if("Payment cancelled by user.".equals(paymentCancel)) {
                Toast.makeText(PaymentPage.this, "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                final DatabaseReference databaseReference = firebaseDatabase.getReference().child("Rooms");
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String userId="";
                        int flag = 0;
                        for(DataSnapshot d:snapshot.getChildren()) {
                            for(DataSnapshot ds:d.getChildren()) {
                                HelperClass snap = ds.getValue(HelperClass.class);
                                if (hlpobj.getRoomName().equals(snap.getRoomName())){
                                    int sep = snapshot.getValue().toString().indexOf("=");
                                    userId = String.valueOf(snapshot.getValue()).substring(1, sep);
                                    flag=1;
                                }
                            }
                            if(flag==1){
                                break;
                            }
                        }
                        String state = "Booked By : Name : " + name.getText().toString() + "Phone : " + phoneNumber.getText().toString() + "From : " +date;
                        HelperClass h1 = new HelperClass(hlpobj.getName(),hlpobj.getAddress(),hlpobj.getCity(),hlpobj.getMinBook(),hlpobj.getMaxBook(),hlpobj.getRent(),state);
                        databaseReference.child(userId).child(hlpobj.getRoomName()).setValue(h1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            else {
                Toast.makeText(PaymentPage.this, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(PaymentPage.this, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable();
        }
        return false;
    }
}