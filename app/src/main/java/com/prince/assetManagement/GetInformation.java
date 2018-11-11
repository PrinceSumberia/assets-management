package com.prince.assetManagement;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class GetInformation extends AppCompatActivity {
    TextView result_id;
    FirebaseAuth mAuth;
    private static final String TAG = GetInformation.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_information);
        FirebaseApp.initializeApp(getApplicationContext());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        result_id = findViewById(R.id.result);

        String user_id = mAuth.getCurrentUser().getUid();
        Log.d(TAG, "User ID " + user_id);
        String complete_id = getIntent().getStringExtra("Decoded Id");
//        result_id.setText(complete_id);

        String[] complete_string = complete_id.split("-");
        String asset_id = complete_string[0];
        final String detectedObject = complete_string[1];
        String id = complete_string[2];

        DocumentReference documentReference = db.collection("users").document(user_id).collection("assets").document(id).collection(detectedObject).document(asset_id);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
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
