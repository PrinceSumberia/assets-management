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



public class FragmentVendor extends Fragment {
    Button get_info;
    EditText editText;
    TextView textView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private static final String TAG = "FragmentVendor";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_vendor, container, false);
        get_info = view.findViewById(R.id.get_info);
        editText = view.findViewById(R.id.asset_vendor);
        textView = view.findViewById(R.id.result);
        assert getArguments() != null;
        final String admin_id = getArguments().getString("admin_id");

        get_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String seller= editText.getText().toString();
                assert admin_id != null;
                db.collection("users")
                        .document(admin_id)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                                        .document(admin_id)
                                        .collection(assets.toString())
                                        .whereEqualTo("seller", seller)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    textView.append(assets.toString() + ": " + task.getResult().size() + "\n");
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
