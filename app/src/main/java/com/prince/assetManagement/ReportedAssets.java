package com.prince.assetManagement;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.widget.LinearLayout.VERTICAL;

public class ReportedAssets extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private ArrayList<String> mAssetType = new ArrayList<>();
    private ArrayList<String> mAssetStatus = new ArrayList<>();
    private ArrayList<String> mReportedBy = new ArrayList<>();
    private static final String TAG = "ReportedAssets";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reported_assets);
        Log.d(TAG, "initRecyclerView: init recycler view");
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        final RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, mAssetType, mAssetStatus, mReportedBy);
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
                            Log.e(TAG, "onComplete: loop is executing");
                            final DocumentSnapshot documentSnapshot = task.getResult();
                            try {
                                Log.e(TAG, "onComplete: assets are " + documentSnapshot.get("assets").toString());
                                for (final Object assets : (ArrayList) documentSnapshot.get("assets")) {
                                    Log.e(TAG, "onComplete: Loop is executing " + assets);
                                    db.collection("users")
                                            .document(user_id)
                                            .collection(assets.toString())
                                            .whereEqualTo("is_working", "false")
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot documentSnapshot1 : task.getResult()) {
                                                            Log.d(TAG, "onComplete: not working assets: " + documentSnapshot1.get("asset_value"));
                                                            Log.d(TAG, "onComplete: not working assets: ");
                                                            mAssetType.add(assets.toString().toUpperCase());
                                                            mAssetStatus.add("Damaged");
                                                            mReportedBy.add(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                                                            adapter.notifyDataSetChanged();
                                                        }

                                                    }
                                                }
                                            });
                                }
                            } catch (NullPointerException e) {
                                Toast.makeText(ReportedAssets.this, "No Damaged Asset Reported", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}
