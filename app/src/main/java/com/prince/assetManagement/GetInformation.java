package com.prince.assetManagement;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Locale;

public class GetInformation extends AppCompatActivity {
    TextView result_id;
    FirebaseAuth mAuth;
    private static final String TAG = GetInformation.class.getName();
    TextView asset_category_field, seller_field, total_quantity_field, purchase_date_field, warranty_date_field, issued_to_field, issued_date_field, textView_issued_date, textView_issued_to;
    Button view_bill, get_geolocation;
    Switch working_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_information);
        FirebaseApp.initializeApp(getApplicationContext());
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        asset_category_field = findViewById(R.id.category);
        seller_field = findViewById(R.id.seller);
        warranty_date_field = findViewById(R.id.date_of_warranty);
        purchase_date_field = findViewById(R.id.date_of_purchase);
        total_quantity_field = findViewById(R.id.number_of_assets);
        issued_to_field = findViewById(R.id.issued_to);
        view_bill = findViewById(R.id.view_bill);
        get_geolocation = findViewById(R.id.get_geolocation);
        issued_date_field = findViewById(R.id.issued_date);
        textView_issued_date = findViewById(R.id.issued_date_textview);
        textView_issued_to = findViewById(R.id.issued_to_textview);
        working_status = findViewById(R.id.change_status);


//        String user_id = mAuth.getCurrentUser().getUid();
//        Log.d(TAG, "User ID " + user_id);
        String complete_id = getIntent().getStringExtra("qrcode_id");
//        result_id.setText(complete_id);

        String[] complete_string = complete_id.split("-");
        String detectedObject = complete_string[0];
        String user_id_asset_id = complete_string[1];

        final String[] user_asset = user_id_asset_id.split("/");
        final String user_id = user_asset[0];
        String asset_id = user_asset[1];


        DocumentReference documentReference = db.collection("users").document(user_id).collection(detectedObject).document(asset_id);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String asset_category = document.get("category").toString();
                        asset_category_field.setText(asset_category);
                        String such_assets = document.get("total_quantity").toString();
                        total_quantity_field.setText(such_assets);
                        String datePurchase = document.get("date_of_purchase").toString();
                        purchase_date_field.setText(datePurchase);
                        String dateWarranty = document.get("warranty").toString();
                        warranty_date_field.setText(dateWarranty);
                        String seller = document.get("seller").toString();
                        seller_field.setText(seller);
                        String issued_to = document.get("issued_to").toString();
                        String department = document.get("department").toString();
                        String room_number = document.get("room").toString();
                        String issued_to_detail = issued_to + "\n" + "Department: " + department + "\nRoom: " + room_number;
                        String issued_date = document.get("issued_date").toString();
                        String isWorking = document.get("is_working").toString();

                        if (isWorking.equals("true")) {
                            working_status.setChecked(true);
                            if (user_id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                working_status.setClickable(true);
                            }
                            Log.e(TAG, "isWorking is true");
                        } else {
                            working_status.setChecked(false);
                        }


                        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    Log.e(TAG, "onComplete: Task is successful");
                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    if (documentSnapshot.exists()) {
                                        Log.e(TAG, "onComplete: Field exists");
                                        String role = documentSnapshot.get("role").toString();
                                        if (role.equals("approver")) {
                                            db.collection("users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if (document.exists()) {
                                                            Log.e(TAG, "onComplete: Field exists");
                                                            List<String> list = (List<String>) document.get("approver");
                                                            for (String uid : list) {
                                                                Log.d("TAG", uid);
                                                                if (uid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                                                    working_status.setClickable(true);
                                                                }
                                                            }

                                                        } else {
                                                            Log.e(TAG, "onComplete: Field Doesn't exists");
                                                        }
                                                    }
                                                }
                                            });
                                        } else if (role.equals("normal user")) {
                                            db.collection("users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        Log.e(TAG, "onComplete: Result" + task.getResult() );
                                                        Log.e(TAG, "onComplete: Result" + task.getResult().getData().toString() );
                                                        Log.e(TAG, "onComplete: Result" + task.getResult().getData().toString() );
                                                        if (document.exists()) {
                                                            Log.e(TAG, "onComplete: Field exists");
                                                            List<String> list = (List<String>) document.get("normal_users");
                                                            for (String uid : list) {
                                                                Log.d("TAG", uid);
                                                                if (uid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                                                    working_status.setClickable(true);
                                                                }
                                                            }

                                                        } else {
                                                            Log.e(TAG, "onComplete: Field Doesn't exists");
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                    }

                                }
                            }
                        });

                        if (issued_to == null) {
                            issued_to_field.setAlpha(0.0f);
                            issued_date_field.setAlpha(0.0f);
                            textView_issued_date.setAlpha(0.0f);
                            textView_issued_to.setAlpha(0.0f);
                        } else {
                            issued_to_field.setText(issued_to_detail);
                            issued_date_field.setText(issued_date);

                        }
                        final String bill_url = document.get("bill").toString();
                        Log.e(TAG, " string url " + bill_url);
                        String[] str_geolocation = document.get("location").toString().split(",");
                        String str_latitude = str_geolocation[0];
                        String str_longitude = str_geolocation[1];

                        final float latitude = Float.parseFloat(str_latitude);
                        final float longitude = Float.parseFloat(str_longitude);


                        view_bill.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse(bill_url));
                                startActivity(intent);
                            }
                        });

                        get_geolocation.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String uri = String.format(Locale.ENGLISH, "geo:%f,%f", latitude, longitude);
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                startActivity(intent);
                            }
                        });


                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

//                        result_id.setText(document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}
