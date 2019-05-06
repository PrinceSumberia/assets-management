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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import static android.widget.LinearLayout.VERTICAL;


public class FragmentDepartment extends Fragment {
    Button get_info, view_analytics;
    EditText editText;
    TextView textView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String TAG = "hello";
    ArrayList<String> mAssetType = new ArrayList<>();
    ArrayList<String> mAssetNumber = new ArrayList<>();
    private Set<String> departmentTreeSet = new TreeSet<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_fragment_department, container, false);
        Log.d(TAG, "initRecyclerView: init recycler view");
        get_info = view.findViewById(R.id.get_info);
        editText = view.findViewById(R.id.department);
        view_analytics = view.findViewById(R.id.view_analytics);

        final RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        final GetInfoList adapter = new GetInfoList(mAssetType, mAssetNumber, getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(), VERTICAL);
        recyclerView.addItemDecoration(decoration);

        assert getArguments() != null;
        final String admin_id = getArguments().getString("admin_id");
        final SwipeRefreshLayout swipeRefreshLayout = new SwipeRefreshLayout(getActivity());
        get_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String department = editText.getText().toString();
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
                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    Log.e(TAG, "onComplete: " + documentSnapshot.get("assets"));
                                    for (final Object assets : (ArrayList) documentSnapshot.get("assets")) {
                                        Log.e(TAG, "onComplete: Inside loop is executing");
                                        db.collection("users")
                                                .document(admin_id)
                                                .collection(assets.toString())
                                                .whereEqualTo("department", department)
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.e(TAG, "onComplete: asset type is " + assets + "Number of assets" + task.getResult().size());
                                                            String number = String.valueOf(task.getResult().size());
                                                            if (Integer.valueOf(number) != 0) {
                                                                mAssetType.add(assets.toString());
                                                                mAssetNumber.add(number);
                                                                adapter.notifyDataSetChanged();
                                                            }
//                                                            textView.append(result.toUpperCase());
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
                Intent intent = new Intent(getActivity(), Display_Graph.class);
                intent.putStringArrayListExtra("AssetType", mAssetType);
                intent.putStringArrayListExtra("AssetNumber", mAssetNumber);
                startActivity(intent);
            }
        });


//        get_info.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                final ArrayList<String> list = new ArrayList<>();
//                db.collection("users").document(user_id).collection("laptop").whereEqualTo("department", editText.getText().toString()).get()
//                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                if (task.isSuccessful()) {
//                                    int count = 0;
//                                    for (QueryDocumentSnapshot document : task.getResult()) {
//                                        String category_name = document.getData().get("category").toString();
//                                        Log.d(TAG, "Here is the result: " + document.getId() + " => " + document.getData());
////                                        textView.setText(document.getData().get("category").toString());
//                                        list.add(document.getData().get("category").toString());
//                                    }
//                                    Set<String> unique = new HashSet<String>(list);
//                                    for (String key : unique) {
//                                        String result = (key + ": " + Collections.frequency(list, key));
//                                        textView.append(result + "\n");
//                                    }
////                                    String result = ":" + list.size();
////                                    textView.append(result);
//                                } else {
//                                    Log.d(TAG, "Error getting documents: ", task.getException());
//                                }
//
//                            }
//                        });
//            }
//        });
        return view;

    }
}
