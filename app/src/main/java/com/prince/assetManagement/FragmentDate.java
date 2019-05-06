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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static android.widget.LinearLayout.VERTICAL;
import static java.util.Collections.sort;


public class FragmentDate extends Fragment {
    Button get_info, view_graph;
    EditText editText;
    TextView textView, textView1, textView2;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private static final String TAG = "FragmentDate";
    ArrayList<String> mAssetType = new ArrayList<>();
    ArrayList<String> mAssetNumber = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_date, container, false);
        Log.d(TAG, "initRecyclerView: init recycler view");
        get_info = view.findViewById(R.id.get_info);
        editText = view.findViewById(R.id.asset_year);
//        textView = view.findViewById(R.id.result_on);
//        textView1 = view.findViewById(R.id.result_before);
//        textView2 = view.findViewById(R.id.result_after);
        view_graph = view.findViewById(R.id.view_graph);

        final RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        final GetInfoList adapter = new GetInfoList(mAssetType, mAssetNumber, getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(), VERTICAL);
        recyclerView.addItemDecoration(decoration);


        assert getArguments() != null;
        final String admin_id = getArguments().getString("admin_id");
        final ArrayList<String> list_year = new ArrayList<>();

//        Date inputDate
        get_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAssetNumber.clear();
                mAssetType.clear();
                adapter.notifyDataSetChanged();
                String date_of_purchase = "20-10-1996";
//                Log.e(TAG, "onClick: split test" + date_of_purchase.split("-").toString());
                Log.e(TAG, "onClick: split test this time" + date_of_purchase.split("-")[2]);
//                Log.e(TAG, "onClick: split test" + date_of_purchase.split("-")[2]);
//                int year = Integer.getInteger(date_of_purchase.split("/")[2]);
//                Log.e(TAG, "onClick: year is" + year);
                assert admin_id != null;
                db.collection("users")
                        .document(admin_id)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    Log.e(TAG, "onComplete: loop is executing");
                                    final DocumentSnapshot documentSnapshot = task.getResult();
                                    Log.e(TAG, "onComplete: assets are " + documentSnapshot.get("assets").toString());
//                                    textView.setTypeface(Typeface.DEFAULT_BOLD);
//                                    textView.setText("In " + editText.getText().toString() + "\n");
//                                    textView.setTypeface(Typeface.DEFAULT);
                                    for (final Object assets : (ArrayList) documentSnapshot.get("assets")) {
                                        Log.e(TAG, "onComplete: Loop is executing " + assets);
                                        db.collection("users")
                                                .document(admin_id)
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
                                                            String number = String.valueOf(task.getResult().size());
//                                                            textView.setText(task.getResult().size());
                                                            if (Integer.valueOf(number) != 0) {
                                                                mAssetType.add(assets.toString().toUpperCase());
                                                                mAssetNumber.add(number);
                                                                Log.e(TAG, "onComplete: Asset list is " + mAssetType.toString());
                                                                Log.e(TAG, "onComplete: Asset Number is " + mAssetNumber.toString());
                                                                adapter.notifyDataSetChanged();
                                                            }
//                                                            textView.append(assets.toString().toUpperCase() + " : " + task.getResult().size() + "\n");
                                                        }
                                                    }
                                                });
                                    }
//                                    textView1.setTypeface(Typeface.DEFAULT_BOLD);
//                                    textView1.setText("\nBefore " + editText.getText().toString() + "\n");
//                                    textView1.setTypeface(Typeface.DEFAULT);
//                                    for (final Object assets : (ArrayList) documentSnapshot.get("assets")) {
//                                        Log.e(TAG, "onComplete: Loop is executing " + assets);
//                                        db.collection("users")
//                                                .document(admin_id)
//                                                .collection(assets.toString())
//                                                .whereLessThan("year", Integer.valueOf(editText.getText().toString()))
//                                                .get()
//                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                                    @Override
//                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                                        if (task.isSuccessful()) {
//                                                            Log.e(TAG, "onComplete: task is successful");
//                                                            Log.e(TAG, "onComplete: edit text is " + editText.getText().toString());
//                                                            Log.e(TAG, "onComplete: edit text is " + Integer.valueOf(editText.getText().toString()));
////                                                            textView.setText(task.getResult().size());
//                                                            textView1.append(assets.toString().toUpperCase() + " : " + task.getResult().size() + "\n");
//                                                        }
//                                                    }
//                                                });
//                                    }
//                                    textView2.setTypeface(Typeface.DEFAULT_BOLD);
//                                    textView2.setText("\nAfter " + editText.getText().toString() + "\n");
//                                    textView2.setTypeface(Typeface.DEFAULT);
//                                    for (final Object assets : (ArrayList) documentSnapshot.get("assets")) {
//                                        Log.e(TAG, "onComplete: Loop is executing " + assets);
//                                        db.collection("users")
//                                                .document(admin_id)
//                                                .collection(assets.toString())
//                                                .whereGreaterThan("year", Integer.valueOf(editText.getText().toString()))
//                                                .get()
//                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                                    @Override
//                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                                        if (task.isSuccessful()) {
//                                                            Log.e(TAG, "onComplete: task is successful");
//                                                            Log.e(TAG, "onComplete: edit text is " + editText.getText().toString());
//                                                            Log.e(TAG, "onComplete: edit text is " + Integer.valueOf(editText.getText().toString()));
////                                                            textView.setText(task.getResult().size());
//                                                            textView2.append(assets.toString().toUpperCase() + " : " + task.getResult().size() + "\n");
//                                                        }
//                                                    }
//                                                });
//                                    }
                                }
                            }
                        });

            }
        });

        view_graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("users")
                        .document(admin_id)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    Log.e(TAG, "onComplete: loop is executing");
                                    final DocumentSnapshot documentSnapshot = task.getResult();
                                    Log.e(TAG, "onComplete: assets are " + documentSnapshot.get("year").toString());
                                    final ArrayList<Long> list = new ArrayList<>();
                                    list.addAll((ArrayList) documentSnapshot.get("year"));
                                    sort(list);
                                    Log.e(TAG, "onComplete: sorted list is " + list);
                                    Log.e(TAG, "onComplete: list is " + list.toString());
                                    for (final Long year : list) {
                                        Log.e(TAG, "onComplete: year is " + year);
                                        db.collection("users")
                                                .document(admin_id)
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            DocumentSnapshot documentSnapshot1 = task.getResult();
                                                            Log.e(TAG, "onComplete: Assets are in year" + year + " is " + documentSnapshot1.get("assets").toString());
                                                            for (final Object assets : (ArrayList) documentSnapshot.get("assets")) {
                                                                db.collection("users")
                                                                        .document(admin_id)
                                                                        .collection(assets.toString())
                                                                        .whereEqualTo("year", year)
                                                                        .get()
                                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                if (task.isSuccessful()) {

                                                                                    if (task.getResult().size() != 0) {
                                                                                        Log.e(TAG, "onComplete: Complete Result is " + assets.toString() + " in year " + year + " is " + task.getResult().size());
                                                                                        for (DocumentSnapshot i : task.getResult()) {
                                                                                            list_year.add(year.toString());
                                                                                            Log.e(TAG, "onComplete: inside for " + year.toString());
                                                                                        }
                                                                                        Log.e(TAG, "onClick: array list year is " + list_year.toString());
//                List<String> list_new =
                                                                                        String[] stringArray = list_year.toArray(new String[0]);
                                                                                        Log.e(TAG, "onClick: new list array" + stringArray.toString());
                                                                                        Intent intent = new Intent(getActivity(), Display_Graph.class);
                                                                                        intent.putExtra("graph", "year");
                                                                                        intent.putStringArrayListExtra("data", list_year);
                                                                                        startActivity(intent);
                                                                                        Log.e(TAG, "onComplete: complete list_year is" + list_year);
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
                            }
                        });

            }

        });
        return view;
    }
}
