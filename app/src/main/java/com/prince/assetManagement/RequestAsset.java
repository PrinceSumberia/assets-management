package com.prince.assetManagement;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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

import java.util.HashMap;
import java.util.Map;

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
                                    String admin_id = documentSnapshot.get("admin_id").toString();
                                    final String name = documentSnapshot.get("name").toString();
                                    final String admin_email = documentSnapshot.get("admin_email").toString();
                                    final Map<String, Object> request_list = new HashMap<>();
                                    final Map<String, Object> requests = new HashMap<>();
                                    final Map<String, Object> request_type = new HashMap<>();
                                    final Map<String, Object> request_details = new HashMap<>();
                                    request_type.put(type, request_details);
                                    request_details.put("asset_number", number);
                                    request_details.put("approved", false);
                                    request_list.put(FirebaseAuth.getInstance().getCurrentUser().getUid(), request_type);
                                    requests.put("requests", request_list);
                                    db.collection("users")
                                            .document(admin_id)
                                            .set(requests, SetOptions.merge())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        ReportAsset(admin_email, name, type, number);
                                                        Log.e(TAG, "onComplete: task is successful");
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

    public void ReportAsset(final String adminEmail, final String name, final String type, final String number) {
        new Thread(new Runnable() {

            public void run() {

                try {

                    GMailSender sender = new GMailSender(
                            "noreply.assetmanagement@gmail.com",
                            "PASSWORDPRINCE");
//                    sender.addAttachment(Environment.getExternalStorageDirectory().getPath() + "/image.jpg");

                    sender.sendMail("Asset Requested", "Asset has been requested by the user. Please login to your admin dashboard. " + "\n" +
                                    "Requested By: " + name.toUpperCase() + "\n" +
                                    "Asset Requested: " + type.toUpperCase() + "\n" +
                                    "Number of Asset Requested: " + number,

                            "noreply.assetmanagement@gmail.com", adminEmail);

//                    Toast.makeText(RequestAsset.this, "Email Sent", Toast.LENGTH_SHORT).show();


                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                }
            }
        }).start();
    }
}
