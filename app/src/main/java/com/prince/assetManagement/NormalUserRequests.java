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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class NormalUserRequests extends AppCompatActivity {
    private static final String TAG = "NormalUserRequests";
    FirebaseFirestore db;
    EditText numberOfAssets, assetType;
    Button requestAsset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_user_requests);
        final String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        numberOfAssets = findViewById(R.id.number_of_assets_req);
        assetType = findViewById(R.id.asset_cat);
        requestAsset = findViewById(R.id.request_assets);

        requestAsset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("users")
                        .document(user_id)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    final DocumentSnapshot documentSnapshot = task.getResult();
                                    if (documentSnapshot.exists()) {
                                        String role = documentSnapshot.get("role").toString();
                                        String admin_id = documentSnapshot.get("admin_id").toString();
                                        final String name = documentSnapshot.get("name").toString();
                                        final String admin_email = documentSnapshot.get("admin_email").toString();
                                        final String str_number_assets = numberOfAssets.getText().toString();
                                        int number_assets = Integer.parseInt(str_number_assets);
                                        final String asset_type = assetType.getText().toString();
                                        final Map<String, Object> asset = new HashMap<>();
                                        asset.put("requestor", name);
                                        asset.put("number", number_assets);
                                        asset.put("asset_type", asset_type);
                                        if (role.equals("normal user")) {
                                            db.collection("users")
                                                    .document(admin_id)
                                                    .update("requests", FieldValue.arrayUnion(asset))
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            ReportAsset(admin_email, asset_type, name, str_number_assets);
                                                        }
                                                    });
                                        }
                                    }
                                }
                            }
                        });
            }
        });
    }

    public void ReportAsset(final String adminEmail, final String requestedAsset, final String requestor, final String number) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    GMailSender sender = new GMailSender(
                            "noreply.assetmanagement@gmail.com",
                            "PASSWORDPRINCE");
//                    sender.addAttachment(Environment.getExternalStorageDirectory().getPath() + "/image.jpg");
                    sender.sendMail("Asset Request", "An asset has been requested by the user." +
                                    "\nRequested Asset:" + requestedAsset + " \n Requested By:" + requestor + "\n" +
                                    "\n Number of Assets: " + number + "\n" +
                                    "Please open your admin dashboard to know about the issue",

                            "noreply.assetmanagement@gmail.com", adminEmail);
                    Log.e(TAG, "run: Email sent successfully");
                    Toast.makeText(NormalUserRequests.this, "Asset Requested", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                }
            }
        }).start();
    }
}
