package com.prince.assetManagement;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

import static android.widget.GridLayout.VERTICAL;

public class ApproverAssetRequest extends AppCompatActivity {
    private static final String TAG = "ApproverAssetRequest";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private ArrayList<String> mAssetType = new ArrayList<>();
    private ArrayList<String> mAssetNumber = new ArrayList<>();
    private ArrayList<String> mRequestedBy = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approver_asset_request);
        Log.d(TAG, "initRecyclerView: init recycler view");
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        final RecyclerViewApproverAdapter adapter = new RecyclerViewApproverAdapter(this, mAssetType, mAssetNumber, mRequestedBy);
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
                            String admin_id = documentSnapshot.get("admin_id").toString();
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
                                                for (Map.Entry<String, Object> entry : requests.entrySet()) {
                                                    Log.d(TAG, "Hello world  " + entry.getKey() + "/" + entry.getValue());
                                                    Map<String, Object> ent = (Map<String, Object>) entry.getValue();
                                                    for (final Map.Entry<String, Object> entr : ent.entrySet()) {
                                                        Log.d(TAG, "onComplete: hello world 2 " + entr.getKey() + "-" + entr.getValue());
                                                        Map<String, String> details = (Map<String, String>) entr.getValue();
                                                        for (final Map.Entry<String, String> req_details : details.entrySet()) {
                                                            Log.d(TAG, "onComplete: the final result is " + req_details.getKey() + "=" + req_details.getValue());
                                                            if (req_details.getKey().equals("asset_number")) {
                                                                db.collection("users")
                                                                        .document(entry.getKey())
                                                                        .get()
                                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    DocumentSnapshot documentSnapshot2 = task.getResult();
                                                                                    String name = documentSnapshot2.get("name").toString();
                                                                                    mRequestedBy.add(name.toUpperCase());
                                                                                    mAssetType.add(entr.getKey().toUpperCase());
                                                                                    mAssetNumber.add(req_details.getValue());
                                                                                    adapter.notifyDataSetChanged();
                                                                                }
                                                                            }
                                                                        });
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    });
                        }
                    }
                });

    }
}
