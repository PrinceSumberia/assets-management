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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;


public class FragmentValue extends Fragment {
    Button get_info;
    EditText editText;
    TextView textView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_value, container, false);
        get_info = view.findViewById(R.id.get_info);
        editText = view.findViewById(R.id.asset_value);
        textView = view.findViewById(R.id.result);


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
                                                    textView.setText(assets.toString() + ": " + task.getResult().size() + "\n");
                                                }
                                            }
                                        });
                            }
//                            Object[] objArray = asset_lists.toArray();
//                            for (int i = 0; i < objArray.length; i++) {
//                                Log.e(TAG, "onComplete: Object array " + objArray[i]);
//                            }
//
//                            Object[] objectsarray = ((ArrayList) documentSnapshot.get("assets")).toArray();
//
//                            for (int i = 0; i < objectsarray.length; i++){
//                                Log.e(TAG, "onComplete: document snapshot array" + objectsarray[i] );
//                            }


//                            String[] stringArray = Arrays.copyOf(asset_lists, asset_lists.size(), String[].class);
//                            String assetlist = documentSnapshot.get("assets").toString();
//                            ArrayList<String> asset_list = new ArrayList<>(Arrays.asList(assetlist.split(",")));
//                            for (String assets: asset_list){
//                                Log.e(TAG, "onComplete: asset is " + assets );
//                            }
                        }
                    }
                });
            }
        });
        return view;
    }
}
