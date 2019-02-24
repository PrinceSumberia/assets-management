package com.prince.assetManagement;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.widget.GridLayout.VERTICAL;

public class ApproverAssetRequest extends AppCompatActivity {
    private static final String TAG = "ApproverAssetRequest";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private ArrayList<String> mAssetType = new ArrayList<>();
    private ArrayList<String> mAssetNumber = new ArrayList<>();
    private ArrayList<String> mRequestedBy = new ArrayList<>();
    private ArrayList<String> mIsAcceptable = new ArrayList<>();
    private ArrayList<String> mRequestorID = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approver_asset_request);
        Log.d(TAG, "initRecyclerView: init recycler view");
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        final RecyclerViewApproverAdapter adapter = new RecyclerViewApproverAdapter(this, mAssetType, mAssetNumber, mRequestedBy, mIsAcceptable, mRequestorID);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
        recyclerView.addItemDecoration(decoration);

        db.collection("users")
                .document(user_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            final String admin_id = documentSnapshot.get("admin_id").toString();
                            db.collection("users")
                                    .document(admin_id)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot documentSnapshot1 = task.getResult();
                                                Map<String, Object> requests = (Map<String, Object>) documentSnapshot1.get("requests");
                                                Log.e(TAG, "onComplete: requests is: " + requests.toString());
                                                Log.e(TAG, "onComplete: requests key: " + requests.keySet() + " requests value: " + requests.values());
                                                for (final Map.Entry<String, Object> entry : requests.entrySet()) {
                                                    Log.d(TAG, "Hello world  " + entry.getKey() + "/" + entry.getValue());
                                                    final Map<String, Object> ent = (Map<String, Object>) entry.getValue();
                                                    for (final Map.Entry<String, Object> entr : ent.entrySet()) {
                                                        Log.d(TAG, "onComplete: hello world 2 " + entr.getKey() + "-" + entr.getValue());
                                                        Map<String, String> details = (Map<String, String>) entr.getValue();
                                                        Log.e(TAG, "onComplete: testing phase" + entr.getKey() + ":" + ((Map<String, String>) entr.getValue()).get("approved"));
                                                        if (((Map<String, String>) entr.getValue()).get("approved").equals("awaiting")) {
                                                            db.collection("users")
                                                                    .document(entry.getKey())
                                                                    .get()
                                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                            if (task.isSuccessful()) {
                                                                                final DocumentSnapshot documentSnapshot2 = task.getResult();
                                                                                final String requestedBy = documentSnapshot2.get("name").toString();
                                                                                final String assetType = entr.getKey();
                                                                                final String assetNumber = ((Map<String, String>) entr.getValue()).get("asset_number");
                                                                                db.collection("users")
                                                                                        .document(admin_id)
                                                                                        .collection(assetType)
                                                                                        .whereEqualTo("issued_to", "None")
                                                                                        .get()
                                                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                                if (task.isSuccessful()) {
                                                                                                    if (Integer.valueOf(assetNumber) > task.getResult().size()) {
                                                                                                        Log.e(TAG, "onComplete: this is executing");
                                                                                                        Log.e(TAG, "onComplete: comparision is " + Integer.valueOf(assetNumber) + "-" + task.getResult().size());
                                                                                                        mIsAcceptable.add("#FF0000");
                                                                                                        mRequestedBy.add(requestedBy.toUpperCase());
                                                                                                        mAssetType.add(assetType.toUpperCase());
                                                                                                        mAssetNumber.add(assetNumber);
                                                                                                        mRequestorID.add(entry.getKey());
                                                                                                        adapter.notifyDataSetChanged();
                                                                                                    } else {
                                                                                                        mRequestedBy.add(requestedBy.toUpperCase());
                                                                                                        mAssetType.add(assetType.toUpperCase());
                                                                                                        mAssetNumber.add(assetNumber);
                                                                                                        mRequestorID.add(entry.getKey());
                                                                                                        mIsAcceptable.add("#008000");
                                                                                                        adapter.notifyDataSetChanged();
                                                                                                    }
                                                                                                    Log.e(TAG, "onComplete: color list is " + mIsAcceptable.toString());
                                                                                                    Log.e(TAG, "onComplete: asset list is " + mAssetType.toString());
                                                                                                    Log.e(TAG, "onComplete: number list is " + mAssetNumber.toString());
                                                                                                } else {
                                                                                                    Log.e(TAG, "onComplete: exception is: " + task.getException().toString());
                                                                                                }
                                                                                            }
                                                                                        });
                                                                            }
                                                                        }
                                                                    });

                                                        }
//
                                                    }
                                                }
                                            }
                                        }
                                    });
                        }
                    }
                });

        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
    }
}
