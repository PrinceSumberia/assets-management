package com.prince.assetManagement;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

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
import java.util.Set;

import static android.support.constraint.Constraints.TAG;

public class ReportedAssets extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
    Button fetch_list;
    String item[] = {"Laptop: By Pr", "TV: By prince"};
    final ArrayList<String> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reported_assets);
//        fetch_list  = findViewById(R.id.fetch_list);
        fetch_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("users")
                        .document(user_id)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    Log.e(TAG, "onComplete: loop is executing");
                                    final DocumentSnapshot documentSnapshot = task.getResult();
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
                                                            items.add(assets.toString());
                                                        }
                                                    }
                                                });
                                    }
                                }
                            }
                        });
            }
        });

        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, item);
        ListView listView = findViewById(R.id.list_item);

        listView.setAdapter(itemsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

//                Toast.makeText(ReportedAssets.this, "Selected Item is: " + selectedItem, Toast.LENGTH_SHORT).show();
//                Log.e(TAG, "Selected Item is: " + selectedItem);
//                Toast.makeText(GetAssetInfo.this, "Selected Item position is: " + position, Toast.LENGTH_SHORT).show();
            }
        });


    }
}
