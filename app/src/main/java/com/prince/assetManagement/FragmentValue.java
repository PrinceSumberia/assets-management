package com.prince.assetManagement;

import android.content.Intent;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;



public class FragmentValue extends Fragment {
    Button get_info, view_graph;
    EditText editText;
    TextView textView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private static final String TAG = "FragmentValue";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_value, container, false);
        get_info = view.findViewById(R.id.get_info);
        editText = view.findViewById(R.id.asset_value);
        textView = view.findViewById(R.id.result);
        view_graph = view.findViewById(R.id.view_graph);
        final ArrayList<String> value = new ArrayList<>();
        final ArrayList<String> asset_array = new ArrayList<>();


        get_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int asset_value = Integer.parseInt(editText.getText().toString());
                db.collection("users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            Log.e(TAG, "onComplete: query document " + documentSnapshot.get("assets").toString());
                            Log.e(TAG, "onComplete: query document " + documentSnapshot.get("assets").getClass().getName());
                            ArrayList asset_lists = (ArrayList) documentSnapshot.get("assets");
                            Log.e(TAG, "onComplete: array list is" + asset_lists.toString());
                            for (final Object assets : ((ArrayList) documentSnapshot.get("assets"))) {
                                Log.e(TAG, "onComplete: objects are" + assets);
                                Log.e(TAG, "onComplete: string objects are" + assets.toString());
                                db.collection("users")
                                        .document(user_id)
                                        .collection(assets.toString())
                                        .whereGreaterThanOrEqualTo("asset_value", asset_value)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    String text = "Assets with higher asset Value\n";
                                                    textView.append(assets.toString() + ": " + task.getResult().size() + "\n");
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
                db.collection("users")
                        .document(user_id)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    final DocumentSnapshot documentSnapshot = task.getResult();
                                    for (final Object assets : (ArrayList) documentSnapshot.get("assets")) {
                                        asset_array.add(assets.toString());
                                        Log.e(TAG, "onComplete: objects are" + assets);
                                        Log.e(TAG, "onComplete: string objects are" + assets.toString());
                                        db.collection("users")
                                                .document(user_id)
                                                .collection(assets.toString())
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        for (QueryDocumentSnapshot documentSnapshot1 : task.getResult()) {
                                                            value.add(documentSnapshot1.get("asset_value").toString());
                                                        }
                                                    }
                                                });
                                    }
                                }
                            }
                        });
            }
        });

        view_graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String[] valueArray = value.toArray(new String[0]);
//                String[] assetsArray = asset_array.toArray(new String[0]);
//                Log.e(TAG, "onClick: new list array" + valueArray.toString() );
                Intent intent = new Intent(getActivity(), Display_Graph.class);
                intent.putExtra("graph", "value");
                intent.putStringArrayListExtra("valueData", value);
                intent.putStringArrayListExtra("assetsData", asset_array);
                startActivity(intent);
            }
        });
        return view;
    }
}
