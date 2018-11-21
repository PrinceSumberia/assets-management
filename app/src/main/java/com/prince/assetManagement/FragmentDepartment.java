package com.prince.assetManagement;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class FragmentDepartment extends Fragment {
    Button get_info;
    EditText editText;
    TextView textView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String TAG = "hello";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_department, container, false);
        get_info = view.findViewById(R.id.get_info);
        editText = view.findViewById(R.id.department);
        textView = view.findViewById(R.id.result);

        get_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ArrayList<String> list = new ArrayList<>();
                db.collection("users").document(user_id).collection("laptop").whereEqualTo("department", editText.getText().toString()).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    int count = 0;
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String category_name = document.getData().get("category").toString();
                                        Log.d(TAG, "Here is the result: " + document.getId() + " => " + document.getData());
//                                        textView.setText(document.getData().get("category").toString());
                                        list.add(document.getData().get("category").toString());
                                    }
                                    Set<String> unique = new HashSet<String>(list);
                                    for (String key : unique) {
                                        String result = (key + ": " + Collections.frequency(list, key));
                                        textView.append(result + "\n");
                                    }
//                                    String result = ":" + list.size();
//                                    textView.append(result);
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }

                            }
                        });
            }
        });
        return view;

    }
}
