package com.prince.assetManagement;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class GetInformation extends AppCompatActivity {
    TextView result_id;
    FirebaseAuth mAuth;
    private static final String TAG = GetInformation.class.getName();
    TextView asset_category_field, seller_field, total_quantity_field, purchase_date_field, warranty_date_field, issued_to_field, issued_date_field, textView_issued_date, textView_issued_to;
    Button view_bill, get_geolocation, report_status;
    Switch working_status;
    boolean before_switch_status;
    String admin_email;

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
        report_status = findViewById(R.id.report_status);

//        report_status.setAlpha(0.0f);


//        String user_id = mAuth.getCurrentUser().getUid();
//        Log.d(TAG, "User ID " + user_id);
        String complete_id = getIntent().getStringExtra("qrcode_id");
        final String[] issued_user_id = new String[1];
//        result_id.setText(complete_id);

        String[] complete_string = complete_id.split("-");
        final String detectedObject = complete_string[0];
        final String user_id_asset_id = complete_string[1];

        final String[] user_asset = user_id_asset_id.split("/");
        final String user_id = user_asset[0];
        final String asset_id = user_asset[1];
//        String issued_user_id;


        DocumentReference documentReference = db.collection("users").document(user_id).collection(detectedObject).document(asset_id);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    final DocumentSnapshot document = task.getResult();
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
                        admin_email = document.get("admin_email").toString();
                        issued_user_id[0] = document.get("issued_to_id").toString();

                        if (isWorking.equals("true")) {
                            working_status.setChecked(true);

                        } else {
                            working_status.setChecked(false);
                        }

                        if (issued_to.equals("")) {
                            issued_to_field.setAlpha(0.0f);
                            issued_date_field.setAlpha(0.0f);
                            textView_issued_date.setAlpha(0.0f);
                            textView_issued_to.setAlpha(0.0f);
                        } else {
                            issued_to_field.setText(issued_to_detail);
                            issued_date_field.setText(issued_date);
                            if (user_id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                report_status.setVisibility(View.VISIBLE);
                            } else if (issued_user_id[0].equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                report_status.setVisibility(View.VISIBLE);
                            } else {
                                Log.e(TAG, "onComplete: this is working but the user is neither admin not normal user of the asset");
                            }
                            Log.e(TAG, "isWorking is true");

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
                                try {
                                    Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                                    String latlong = latitude + "," + longitude;
                                    intent.putExtra("latlong", latlong);
                                    startActivity(intent);
                                } catch (Exception e) {
                                    Toast.makeText(GetInformation.this, "Error Getting Location", Toast.LENGTH_SHORT).show();
                                }
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


        report_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("users")
                        .document(user_id)
                        .collection(detectedObject)
                        .document(asset_id)
                        .update("is_working", "false",
                                "reported_by", FirebaseAuth.getInstance().getCurrentUser().getDisplayName())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    db.collection("users")
                                            .document(issued_user_id[0])
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot documentSnapshot = task.getResult();
                                                        String issued_username = documentSnapshot.get("name").toString();
                                                        Log.e(TAG, "onComplete: the admin email field is " + admin_email);
                                                        ReportAsset(admin_email, asset_category_field.getText().toString(), issued_username);
                                                        Toast.makeText(GetInformation.this, "Asset Reported", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                } else {
                                    Log.e(TAG, "onComplete: Task is unsuccessful" + task.getException());
                                    Toast.makeText(GetInformation.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
//        report_status.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                final String email = "broughtmeup@gmail.com";
//                new Thread(new Runnable() {
//
//                    public void run() {
//
//                        try {
//
//                            GMailSender sender = new GMailSender(
//                                    "noreply.assetmanagement@gmail.com",
//                                    "PASSWORDPRINCE");
////                    sender.addAttachment(Environment.getExternalStorageDirectory().getPath() + "/image.jpg");
//
//                            sender.sendMail("Asset Reported", "An asset has been reported by the user. It may not be damaged or not working properly. Please open your admin dashboard",
//
//                                    "noreply.assetmanagement@gmail.com", email);
//                            Log.e(TAG, "run: " +email );
//                            Log.e(TAG, "run: email status sent");
//
//
//                        } catch (Exception e) {
//                            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
//                        }
//                    }
//                }).start();
//            }
//        });
    }

    public void ReportAsset(final String adminEmail, final String reportedAsset, final String reporter) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    GMailSender sender = new GMailSender(
                            "noreply.assetmanagement@gmail.com",
                            "PASSWORDPRINCE");
//                    sender.addAttachment(Environment.getExternalStorageDirectory().getPath() + "/image.jpg");
                    sender.sendMail("Asset Reported", "An asset has been reported by the user. It may be damaged or not working properly. " +
                                    "\nReported Asset:" + reportedAsset + " \n Reported By:" + reporter + "\n" +
                                    "Please open your admin dashboard to know about the issue",

                            "noreply.assetmanagement@gmail.com", adminEmail);
                    Log.e(TAG, "run: email status sent");
//                    Toast.makeText(GetInformation.this, "Asset Reported", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                }
            }
        }).start();
    }
}
