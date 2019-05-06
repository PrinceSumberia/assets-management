package com.prince.assetManagement;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

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
import java.util.List;
import java.util.Map;

import static android.widget.LinearLayout.VERTICAL;


public class FragmentAdmin extends Fragment {
    private static final String TAG = "FragmentAdmin";
    Button get_info;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
    Spinner spinner;
    ArrayList<String> mAssetType = new ArrayList<>();
    ArrayList<String> mAssetNumber = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_admin, container, false);
        Log.d(TAG, "initRecyclerView: init recycler view");
        get_info = view.findViewById(R.id.get_info);
        spinner = view.findViewById(R.id.spinner_admin);

        final String[] addedById = new String[1];
        final List<String> admin_name = new ArrayList<>();
        final List<String> admin_user_id = new ArrayList<>();

        assert getArguments() != null;
        final String admin_id = getArguments().getString("admin_id");

        final RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        final GetInfoList adapter = new GetInfoList(mAssetType, mAssetNumber, getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(), VERTICAL);
        recyclerView.addItemDecoration(decoration);

        assert admin_id != null;
        db.collection("users")
                .document(admin_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            Map<String, String> users = (Map<String, String>) documentSnapshot.get("admin_users");
                            for (Map.Entry<String, String> user : users.entrySet()) {
                                Log.e(TAG, "onComplete: the user is: " + user.getKey() + " id is: " + user.getValue());
                                admin_name.add(user.getKey().toUpperCase());
                                admin_user_id.add(user.getValue());
                            }
                            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, admin_name);
                            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(dataAdapter);
                        }
                    }
                });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                addedById[0] = admin_user_id.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        get_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAssetType.clear();
                mAssetNumber.clear();
                adapter.notifyDataSetChanged();
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
                                    for (final Object assets : (ArrayList) documentSnapshot.get("assets")) {
                                        Log.e(TAG, "onComplete: Loop is executing " + assets);
                                        db.collection("users")
                                                .document(admin_id)
                                                .collection(assets.toString())
                                                .whereEqualTo("added_by_admin", addedById[0])
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            String number = String.valueOf(task.getResult().size());
                                                            if (Integer.valueOf(number) != 0) {
                                                                mAssetType.add(assets.toString().toUpperCase());
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
            }
        });

        return view;
    }
}
