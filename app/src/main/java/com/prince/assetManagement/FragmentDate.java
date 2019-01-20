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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;


public class FragmentDate extends Fragment {
    Button get_info;
    EditText editText;
    TextView textView, textView1, textView2;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_date, container, false);
        get_info = view.findViewById(R.id.get_info);
        editText = view.findViewById(R.id.asset_year);
        textView = view.findViewById(R.id.result_on);
        textView1 = view.findViewById(R.id.result_before);
        textView2 = view.findViewById(R.id.result_after);

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
                                    textView.setTypeface(Typeface.DEFAULT_BOLD);
                                    textView.setText("In " + editText.getText().toString() + "\n");
                                    textView.setTypeface(Typeface.DEFAULT);
                                    for (final Object assets : (ArrayList) documentSnapshot.get("assets")) {
                                        Log.e(TAG, "onComplete: Loop is executing " + assets);
                                        db.collection("users")
                                                .document(user_id)
                                                .collection(assets.toString())
                                                .whereEqualTo("year", Integer.valueOf(editText.getText().toString()))
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.e(TAG, "onComplete: task is successful");
                                                            Log.e(TAG, "onComplete: edit text is " + editText.getText().toString());
                                                            Log.e(TAG, "onComplete: edit text is " + Integer.valueOf(editText.getText().toString()));
//                                                            textView.setText(task.getResult().size());
                                                            textView.append(assets.toString().toUpperCase() + " : " + task.getResult().size() + "\n");
                                                        }
                                                    }
                                                });
                                    }
                                    textView1.setTypeface(Typeface.DEFAULT_BOLD);
                                    textView1.setText("\nBefore " + editText.getText().toString() + "\n");
                                    textView1.setTypeface(Typeface.DEFAULT);
                                    for (final Object assets : (ArrayList) documentSnapshot.get("assets")) {
                                        Log.e(TAG, "onComplete: Loop is executing " + assets);
                                        db.collection("users")
                                                .document(user_id)
                                                .collection(assets.toString())
                                                .whereLessThan("year", Integer.valueOf(editText.getText().toString()))
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.e(TAG, "onComplete: task is successful");
                                                            Log.e(TAG, "onComplete: edit text is " + editText.getText().toString());
                                                            Log.e(TAG, "onComplete: edit text is " + Integer.valueOf(editText.getText().toString()));
//                                                            textView.setText(task.getResult().size());
                                                            textView1.append(assets.toString().toUpperCase() + " : " + task.getResult().size() + "\n");
                                                        }
                                                    }
                                                });
                                    }
                                    textView2.setTypeface(Typeface.DEFAULT_BOLD);
                                    textView2.setText("\nAfter " + editText.getText().toString() + "\n");
                                    textView2.setTypeface(Typeface.DEFAULT);
                                    for (final Object assets : (ArrayList) documentSnapshot.get("assets")) {
                                        Log.e(TAG, "onComplete: Loop is executing " + assets);
                                        db.collection("users")
                                                .document(user_id)
                                                .collection(assets.toString())
                                                .whereGreaterThan("year", Integer.valueOf(editText.getText().toString()))
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.e(TAG, "onComplete: task is successful");
                                                            Log.e(TAG, "onComplete: edit text is " + editText.getText().toString());
                                                            Log.e(TAG, "onComplete: edit text is " + Integer.valueOf(editText.getText().toString()));
//                                                            textView.setText(task.getResult().size());
                                                            textView2.append(assets.toString().toUpperCase() + " : " + task.getResult().size() + "\n");
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
