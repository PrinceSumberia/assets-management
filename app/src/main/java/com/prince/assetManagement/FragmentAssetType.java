package com.prince.assetManagement;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static android.widget.LinearLayout.VERTICAL;


public class FragmentAssetType extends Fragment {
    Button get_info;
    EditText editText;
    TextView textView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private static final String TAG = "FragmentAssetType";
    ArrayList<String> mAssetType = new ArrayList<>();
    ArrayList<String> mAssetNumber = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_asset_type, container, false);
        get_info = view.findViewById(R.id.get_info);
        editText = view.findViewById(R.id.asset_type);
        textView = view.findViewById(R.id.result);

        final RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        final GetInfoDepList adapter = new GetInfoDepList(mAssetType, mAssetNumber, getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(), VERTICAL);
        recyclerView.addItemDecoration(decoration);

        assert getArguments() != null;
        final String admin_id = getArguments().getString("admin_id");
        get_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String asset_type = editText.getText().toString();
                final ArrayList<String> list = new ArrayList<>();
                final ArrayList<String> new_list = new ArrayList<>();
                assert admin_id != null;
                db.collection("users")
                        .document(admin_id)
                        .collection(asset_type)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    final int number_assets = task.getResult().size();
                                    String str_assets = String.valueOf(number_assets);
                                    String result = "Number of such assets: " + str_assets;
                                    //textView.setText(result);
                                    Log.e(TAG, "onComplete: total assets is " + result );
                                    for (QueryDocumentSnapshot querySnapshot : task.getResult()) {
                                        Log.e(TAG, "onComplete: querysnapshot is " + task.getResult().size() );
                                        Log.e(TAG, "onComplete: querysnapshot is " + task.getResult().getDocuments().toString() );
                                        Log.e(TAG, "onComplete: querysnapshot is " + task.getResult().getMetadata() );
                                        Log.e(TAG, "onComplete: Query snapshot " + querySnapshot.getData().toString() );
                                        Log.e(TAG, "onComplete: Query snapshot " + querySnapshot.getData().get("department") );
                                        list.add(querySnapshot.getData().get("department").toString());
                                    }
                                    Set<String> unique = new HashSet<>(list);
//                                    for (String key : unique) {
////                                        String unique_result = (key + ": " + Collections.frequency(list, key));
//                                        String unique_result = (key + ": " + Collections.frequency(list, key));
//                                        new_list.add(unique_result);
//                                    }
                                    for (final String dep : unique) {
                                        Log.e(TAG, "onComplete: dep is" + dep);
                                        Log.e(TAG, "onComplete: Inner loop is getting executed");
                                        db.collection("users")
                                                .document(admin_id)
                                                .collection(asset_type)
                                                .whereEqualTo("department", dep).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()){
//                                                    textView.append("\n" + dep.toUpperCase() + " Department: " + task.getResult().size() );
                                                    String number = String.valueOf(task.getResult().size());
                                                    if (Integer.valueOf(number) != 0) {
                                                        mAssetType.add(dep.toUpperCase());
                                                        mAssetNumber.add(number);
                                                        adapter.notifyDataSetChanged();
                                                    }

                                                    Log.e(TAG, "onComplete: asset in department " + dep +" is " + task.getResult().size());
                                                    Log.e(TAG, "onComplete: asset in department " + dep +" is " + task.getResult().toString());
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    Log.e(TAG, "onComplete: Task is unsuccessful " + task.getException().toString());
                                }
                            }
                        });
            }
        });

        return view;
    }
}