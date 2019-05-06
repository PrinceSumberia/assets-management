package com.prince.assetManagement;

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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.widget.LinearLayout.VERTICAL;


public class FragmentUser extends Fragment {
    Button get_info;
    EditText editText;
    TextView textView, textView1;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private static final String TAG = "FragmentUser";
    Spinner spinner;
    ArrayList<String> mAssetType = new ArrayList<>();
    ArrayList<String> mAssetNumber = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        Log.d(TAG, "initRecyclerView: init recycler view");
        get_info = view.findViewById(R.id.get_info);
//        editText = view.findViewById(R.id.asset_user);
//        textView = view.findViewById(R.id.result);
//        textView1 = view.findViewById(R.id.result_title);
        spinner = view.findViewById(R.id.spinner_user);

        final List<String> username = new ArrayList<>();
        final List<String> user_id = new ArrayList<>();

        assert getArguments() != null;
        final String admin_id = getArguments().getString("admin_id");
        final String[] issued_to_id = new String[1];
        Log.d(TAG, "onCreateView: user id " + issued_to_id[0]);

        final RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        final GetInfoList adapter = new GetInfoList(mAssetType, mAssetNumber, getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(), VERTICAL);
        recyclerView.addItemDecoration(decoration);


        db.collection("users")
                .document(admin_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            Map<String, String> users = (Map<String, String>) documentSnapshot.get("normal_users");
                            for (Map.Entry<String, String> user : users.entrySet()) {
                                Log.e(TAG, "onComplete: the user is: " + user.getKey() + " id is: " + user.getValue());
                                username.add(user.getKey().toUpperCase());
                                user_id.add(user.getValue());
                            }
                            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, username);
                            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(dataAdapter);
                        }
                    }
                });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                issued_to_id[0] = user_id.get(i);
                Log.d(TAG, "onItemSelected: user id is " + issued_to_id[0]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        get_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assert admin_id != null;
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
                                                .whereEqualTo("issued_to_id", issued_to_id[0])
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            final ArrayList<String> list = new ArrayList<>();
                                                            Log.e(TAG, "onComplete: task is successful");
                                                            Log.e(TAG, "onComplete: executing" + task.getResult().getDocuments().toString());
                                                            Log.e(TAG, "onComplete: " + task.getResult().getMetadata().toString());
//                                                            textView1.setText(assets.toString().toUpperCase());
                                                            String result = "Quantity: " + task.getResult().size() + "\n";
                                                            String number = String.valueOf(task.getResult().size());
                                                            if (Integer.valueOf(number) != 0) {
                                                                mAssetType.add(assets.toString().toUpperCase());
                                                                mAssetNumber.add(number);
                                                                adapter.notifyDataSetChanged();
                                                            }
//                                                            textView.setText(result);
//                                                            textView.append("Date Issued: \n");

                                                            int count = 0;
//                                                            for (QueryDocumentSnapshot documentSnapshot1 : task.getResult()) {
//                                                                list.add(documentSnapshot1.getData().get("issued_date").toString());
//                                                                Log.e(TAG, "onComplete: document snapshot " + documentSnapshot1.getData().get("issued_date").toString());
//                                                            }
//                                                            Set<String> unique = new HashSet<>(list);
//                                                            for (String key : unique) {
//                                                                String new_result = (Collections.frequency(list, key) + " on " + key);
//                                                                textView.append("   \u2022 " + new_result + "\n");
//                                                            }
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
