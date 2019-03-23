package com.prince.assetManagement;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.widget.LinearLayout.VERTICAL;

public class IssuedAssets extends AppCompatActivity {
    private static final String TAG = "IssuedAssets";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String admin_user_id;
    ArrayList<String> mAssetType = new ArrayList<>();
    ArrayList<String> mAssetNumber = new ArrayList<>();
    ArrayList<String> mIssuedDate = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issued_assets);

        final String current_user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        Log.d(TAG, "initRecyclerView: init recycler view");
        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        final IssuedAssetsAdapter adapter = new IssuedAssetsAdapter(this, mAssetType, mAssetNumber, mIssuedDate);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration decoration = new DividerItemDecoration(this, VERTICAL);
        recyclerView.addItemDecoration(decoration);

        db.collection("users")
                .document(current_user_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            assert documentSnapshot != null;
                            admin_user_id = Objects.requireNonNull(documentSnapshot.get("admin_id")).toString();
                            db.collection("users")
                                    .document(admin_user_id)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot documentSnapshot1 = task.getResult();
                                                assert documentSnapshot1 != null;
                                                for (final Object assets : (ArrayList) Objects.requireNonNull(documentSnapshot1.get("assets"))) {
                                                    documentSnapshot1.getReference()
                                                            .collection(assets.toString())
                                                            .whereEqualTo("issued_to_id", current_user_id)
                                                            .get()
                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Log.d(TAG, "onComplete: Getting issued data");
                                                                        ArrayList<String> list = new ArrayList<>();
                                                                        for (QueryDocumentSnapshot queryDocumentSnapshot : Objects.requireNonNull(task.getResult())) {
                                                                            String date = Objects.requireNonNull(queryDocumentSnapshot.getData().get("issued_date")).toString();
                                                                            list.add(date);
                                                                        }
                                                                        Log.e(TAG, "onComplete: List is " + list.toString());
                                                                        Set<String> unique = new HashSet<>(list);
                                                                        for (String key : unique) {
                                                                            String result = (key + ": " + Collections.frequency(list, key));
                                                                            String number = String.valueOf(Collections.frequency(list, key));
                                                                            Log.e(TAG, "onComplete: result is: " + result);
                                                                            mAssetNumber.add(number);
                                                                            mAssetType.add(assets.toString().toUpperCase());
                                                                            mIssuedDate.add(key);
                                                                            adapter.notifyDataSetChanged();
                                                                        }
                                                                    }
                                                                }
                                                            });
                                                }
                                            }
                                        }
                                    });
                        }
                    }
                });

    }
}
