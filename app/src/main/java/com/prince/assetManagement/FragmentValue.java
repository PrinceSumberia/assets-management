package com.prince.assetManagement;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static android.widget.LinearLayout.VERTICAL;


public class FragmentValue extends Fragment {
    Button get_info, view_analytics;
    EditText editText;
    TextView textView;
    final private List<String> assetValue = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private static final String TAG = "FragmentValue";
    Spinner spinner;
    ArrayList<String> mAssetType = new ArrayList<>();
    ArrayList<String> mAssetNumber = new ArrayList<>();
    private String condition;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_value, container, false);
        Log.d(TAG, "initRecyclerView: init recycler view");
        get_info = view.findViewById(R.id.get_info);
        editText = view.findViewById(R.id.asset_value);
//        textView = view.findViewById(R.id.result);
        view_analytics = view.findViewById(R.id.view_graph);
        spinner = view.findViewById(R.id.spinner_value);


        final RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        final GetInfoList adapter = new GetInfoList(mAssetType, mAssetNumber, getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(), VERTICAL);
        recyclerView.addItemDecoration(decoration);

        assetValue.add("=");
        assetValue.add(">");
        assetValue.add("<");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, assetValue);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        assert getArguments() != null;
        final String admin_id = getArguments().getString("admin_id");
        final ArrayList<String> value = new ArrayList<>();
        final ArrayList<String> asset_array = new ArrayList<>();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                condition = assetValue.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        get_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int asset_value = Integer.parseInt(editText.getText().toString());
                assert admin_id != null;
                mAssetType.clear();
                mAssetNumber.clear();
                adapter.notifyDataSetChanged();
                switch (condition) {
                    case ">":
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
                                                        .whereGreaterThan("asset_value", asset_value)
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                if (task.isSuccessful()) {
//                                                                String text = "Assets with higher asset Value\n";
                                                                    String number = String.valueOf(task.getResult().size());
                                                                    if (Integer.valueOf(number) != 0) {
                                                                        mAssetType.add(assets.toString());
                                                                        mAssetNumber.add(number);
                                                                        adapter.notifyDataSetChanged();
                                                                    }
//                                                                textView.append(assets.toString() + ": " + task.getResult().size() + "\n");
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    }
                                });
                        break;
                    case "<":
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
                                                        .whereLessThan("asset_value", asset_value)
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    String number = String.valueOf(task.getResult().size());
                                                                    if (Integer.valueOf(number) != 0) {
                                                                        mAssetType.add(assets.toString());
                                                                        mAssetNumber.add(number);
                                                                        adapter.notifyDataSetChanged();
                                                                    }
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    }
                                });
                        break;
                    case "=":
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
                                                        .whereEqualTo("asset_value", asset_value)
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    String number = String.valueOf(task.getResult().size());
                                                                    if (Integer.valueOf(number) != 0) {
                                                                        mAssetType.add(assets.toString());
                                                                        mAssetNumber.add(number);
                                                                        adapter.notifyDataSetChanged();
                                                                    }

                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    }
                                });
                        break;
                }

                db.collection("users")
                        .document(admin_id)
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
                                                .document(admin_id)
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

        view_analytics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AssetValueGraph.class);
                intent.putStringArrayListExtra("AssetType", mAssetType);
                intent.putStringArrayListExtra("AssetNumber", mAssetNumber);
                startActivity(intent);
            }
        });
        return view;
    }
}
