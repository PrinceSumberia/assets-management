package com.prince.assetManagement;

import android.os.Bundle;
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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


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

        assert getArguments() != null;
        final String admin_id = getArguments().getString("admin_id");

        get_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String department = editText.getText().toString();
                assert admin_id != null;
                db.collection("users")
                        .document(admin_id)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    Log.e(TAG, "onComplete: " + documentSnapshot.get("assets"));
                                    for (final Object assets : (ArrayList) documentSnapshot.get("assets")) {
                                        Log.e(TAG, "onComplete: Inside lopp is executing");
                                        db.collection("users")
                                                .document(admin_id)
                                                .collection(assets.toString())
                                                .whereEqualTo("department", department)
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.e(TAG, "onComplete: asset type is " + assets + "Number of assets" + task.getResult().size());
                                                            String result = assets + ": " + task.getResult().size() + "" + "\n";
                                                            textView.append(result.toUpperCase());
                                                        }
                                                    }
                                                });
                                    }
                                }
                            }
                        });
            }
        });

//        get_info.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                final ArrayList<String> list = new ArrayList<>();
//                db.collection("users").document(user_id).collection("laptop").whereEqualTo("department", editText.getText().toString()).get()
//                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                if (task.isSuccessful()) {
//                                    int count = 0;
//                                    for (QueryDocumentSnapshot document : task.getResult()) {
//                                        String category_name = document.getData().get("category").toString();
//                                        Log.d(TAG, "Here is the result: " + document.getId() + " => " + document.getData());
////                                        textView.setText(document.getData().get("category").toString());
//                                        list.add(document.getData().get("category").toString());
//                                    }
//                                    Set<String> unique = new HashSet<String>(list);
//                                    for (String key : unique) {
//                                        String result = (key + ": " + Collections.frequency(list, key));
//                                        textView.append(result + "\n");
//                                    }
////                                    String result = ":" + list.size();
////                                    textView.append(result);
//                                } else {
//                                    Log.d(TAG, "Error getting documents: ", task.getException());
//                                }
//
//                            }
//                        });
//            }
//        });
        return view;

    }
}
