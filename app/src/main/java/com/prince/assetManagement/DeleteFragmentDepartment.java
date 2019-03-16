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


public class DeleteFragmentDepartment extends Fragment {
    private static final String TAG = "DeleteFragmentDepartmen";
    Button get_info, view_graph;
    EditText editText, editText2;
    TextView textView, textView1, textView2;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_delete_fragment_department, container, false);
        get_info = view.findViewById(R.id.get_info);
        editText = view.findViewById(R.id.asset_year);
        textView = view.findViewById(R.id.result_on);
        textView1 = view.findViewById(R.id.result_before);
        textView2 = view.findViewById(R.id.result_after);
        view_graph = view.findViewById(R.id.view_graph);
        editText2 = view.findViewById(R.id.delete_asset_type);
        final ArrayList<String> list_year = new ArrayList<>();

        assert getArguments() != null;
        final String admin_id = getArguments().getString("admin_id");

//        Date inputDate
        get_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String date_of_purchase = "20-10-1996";
//                Log.e(TAG, "onClick: split test" + date_of_purchase.split("-").toString());
                Log.e(TAG, "onClick: split test this time" + date_of_purchase.split("-")[2]);
//                Log.e(TAG, "onClick: split test" + date_of_purchase.split("-")[2]);
//                int year = Integer.getInteger(date_of_purchase.split("/")[2]);
//                Log.e(TAG, "onClick: year is" + year);
                assert admin_id != null;
                db.collection("users")
                        .document(admin_id)
                        .collection(editText2.getText().toString())
                        .whereLessThan("year", Integer.valueOf(editText.getText().toString()))
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
//                                        Log.e(TAG, "onComplete: the result is " + "year "+ editText.getText().toString() + documentSnapshot.getId());
//                                        Log.e(TAG, "onComplete: delete query is " + documentSnapshot.getReference().toString());
                                        documentSnapshot.getReference().delete();
                                    }
                                }
                            }
                        });
            }
        });

        return view;
    }
}
