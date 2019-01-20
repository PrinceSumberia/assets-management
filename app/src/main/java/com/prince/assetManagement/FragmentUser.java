package com.prince.assetManagement;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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


public class FragmentUser extends Fragment {
    Button get_info;
    EditText editText;
    TextView textView, textView1;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        get_info = view.findViewById(R.id.get_info);
        editText = view.findViewById(R.id.asset_user);
        textView = view.findViewById(R.id.result);
        textView1 = view.findViewById(R.id.result_title);

        get_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String issued_to = editText.getText().toString();
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
                                                .whereEqualTo("issued_to", issued_to)
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            final ArrayList<String> list = new ArrayList<>();
                                                            Log.e(TAG, "onComplete: task is successful");
                                                            Log.e(TAG, "onComplete: executing" + task.getResult().getDocuments().toString());
                                                            Log.e(TAG, "onComplete: " + task.getResult().getMetadata().toString());
                                                            textView1.setText(assets.toString().toUpperCase());
                                                            String result = "Quantity: " + task.getResult().size() + "\n";
                                                            textView.setText(result);
                                                            textView.append("Date Issued: \n");
                                                            int count = 0;
                                                            for (QueryDocumentSnapshot documentSnapshot1 : task.getResult()) {
                                                                list.add(documentSnapshot1.getData().get("issued_date").toString());
                                                                Log.e(TAG, "onComplete: document snapshot " + documentSnapshot1.getData().get("issued_date").toString());
                                                            }
                                                            Set<String> unique = new HashSet<String>(list);
                                                            for (String key : unique) {
                                                                String new_result = (Collections.frequency(list, key) + " on " + key);
                                                                textView.append("   \u2022 " + new_result + "\n");
                                                            }
                                                        }
                                                    }
                                                });
                                    }
                                }
                            }
                        });
            }
        });

        return view;
    }
}
