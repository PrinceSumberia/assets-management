package com.prince.assetManagement;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class RequestAsset extends AppCompatActivity {
    EditText assetType, assetNumber;
    Button submit;
    private static final String TAG = "RequestAsset";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_asset);

        assetType = findViewById(R.id.asset_type);
        assetNumber = findViewById(R.id.asset_no);
        submit = findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String type = assetType.getText().toString();
                final String number = assetNumber.getText().toString();
                db.collection("users")
                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    final String admin_id = documentSnapshot.get("admin_id").toString();
                                    final String name = documentSnapshot.get("name").toString();
                                    final String admin_email = documentSnapshot.get("admin_email").toString();
                                    final Map<String, Object> request_list = new HashMap<>();
                                    final Map<String, Object> requests = new HashMap<>();
                                    final Map<String, Object> request_type = new HashMap<>();
                                    final Map<String, Object> request_details = new HashMap<>();
                                    request_type.put(type, request_details);
                                    request_details.put("asset_number", number);
                                    Calendar calendar = Calendar.getInstance();
                                    SimpleDateFormat mdformat = new SimpleDateFormat("dd/MM/yyyy");
                                    final String strDate = mdformat.format(calendar.getTime());
                                    Log.e(TAG, "Current Date: " + strDate);
                                    request_details.put("date", strDate);
                                    request_details.put("approved", "awaiting");
                                    request_details.put("user_email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                    request_list.put(FirebaseAuth.getInstance().getCurrentUser().getUid(), request_type);
                                    requests.put("requests", request_list);
                                    db.collection("users")
                                            .document(admin_id)
                                            .set(requests, SetOptions.merge())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        db.collection("users")
                                                                .document(admin_id)
                                                                .get()
                                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                        DocumentSnapshot documentSnapshot1 = task.getResult();
                                                                        String result = documentSnapshot1.get("approver").toString();
                                                                        Log.e(TAG, "onComplete: string result is " + result);
                                                                        Map<String, String> approver_list = (Map<String, String>) documentSnapshot1.get("approver");
                                                                        Log.e(TAG, "onComplete: map result is " + approver_list);
                                                                        Log.e(TAG, "onComplete: the complete result " + approver_list.keySet() + " - " + approver_list.values());
                                                                        for (Map.Entry<String, String> final_approver_list : approver_list.entrySet()) {
                                                                            Log.e(TAG, "onComplete: loop is starting " + final_approver_list.getKey() + " : " + final_approver_list.getValue());
                                                                            db.collection("users")
                                                                                    .document(final_approver_list.getValue())
                                                                                    .get()
                                                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                            DocumentSnapshot documentSnapshot2 = task.getResult();
                                                                                            if (task.isSuccessful()) {
                                                                                                String approver_email = documentSnapshot2.get("email").toString();
                                                                                                RequestAssetMail(approver_email, name, type, number);
                                                                                                Toast.makeText(RequestAsset.this, "Asset Requested Successfully.", Toast.LENGTH_SHORT).show();
                                                                                            }
                                                                                        }
                                                                                    });
                                                                        }
                                                                        Log.e(TAG, "onComplete: task is successful");
                                                                    }
                                                                });
                                                    } else {
                                                        Log.e(TAG, "onComplete: task is unsuccessful " + task.getException().toString());
                                                    }
                                                }
                                            });
                                }
                            }
                        });
            }
        });
    }

    public void RequestAssetMail(final String approverEmail, final String name, final String type, final String number) {
        new Thread(new Runnable() {

            public void run() {

                try {

                    GMailSender sender = new GMailSender(
                            "noreply.assetmanagement@gmail.com",
                            "PASSWORDPRINCE");
//                    sender.addAttachment(Environment.getExternalStorageDirectory().getPath() + "/image.jpg");

                    sender.sendMail("Asset Requested", "Asset has been requested by the user. Please login to your dashboard. " + "\n" +
                                    "Requested By: " + name.toUpperCase() + "\n" +
                                    "Asset Requested: " + type.toUpperCase() + "\n" +
                                    "Number of Asset Requested: " + number,

                            "noreply.assetmanagement@gmail.com", approverEmail);

//                    Toast.makeText(RequestAsset.this, "Email Sent", Toast.LENGTH_SHORT).show();


                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                }
            }
        }).start();
    }
}
