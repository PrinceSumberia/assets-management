package com.prince.assetManagement;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.widget.GridLayout.VERTICAL;

public class AdminAssetRequest extends AppCompatActivity {
    private static final String TAG = "AdminAssetRequest";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ArrayList<String> mAssetType = new ArrayList<>();
    private ArrayList<String> mAssetNumber = new ArrayList<>();
    private ArrayList<String> mRequestedBy = new ArrayList<>();
    private ArrayList<String> mRequestorID = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_asset_request);
        Log.d(TAG, "initRecyclerView: init recycler view");
        String admin_id = getIntent().getStringExtra("admin_id");
        String admin_email = getIntent().getStringExtra("admin_email");
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        final RecyclerViewAdminAssetRequestAdapter adapter = new RecyclerViewAdminAssetRequestAdapter(this, mAssetType, mAssetNumber, mRequestedBy, mRequestorID);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
        recyclerView.addItemDecoration(decoration);


        db.collection("users")
                .document(admin_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot1 = task.getResult();
                            try {
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
                                        if (((Map<String, String>) entr.getValue()).get("approved").equals("true")) {
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
                                                                mRequestedBy.add(requestedBy.toUpperCase());
                                                                mAssetType.add(assetType.toUpperCase());
                                                                mAssetNumber.add(assetNumber);
                                                                mRequestorID.add(entry.getKey());
                                                                if (mAssetType.isEmpty()) {
                                                                    Toast.makeText(AdminAssetRequest.this, "No Asset Requests", Toast.LENGTH_SHORT).show();
                                                                }
                                                                adapter.notifyDataSetChanged();
                                                            }
                                                        }
                                                    });

                                        }
//
                                    }

                                }
                            } catch (NullPointerException e) {
                                Toast.makeText(AdminAssetRequest.this, "No Asset Requests", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

    }
}
