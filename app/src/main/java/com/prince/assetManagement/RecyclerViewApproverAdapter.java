package com.prince.assetManagement;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class RecyclerViewApproverAdapter extends RecyclerView.Adapter<RecyclerViewApproverAdapter.ViewHolder> {
    private static final String TAG = "RecyclerViewApproverAdapter";
    private ArrayList<String> mAssetType = new ArrayList<>();
    private ArrayList<String> mAssetNumber = new ArrayList<>();
    private ArrayList<String> mRequestedBy = new ArrayList<>();
    private ArrayList<String> mIsAcceptable = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Context mContext;
    private ArrayList<String> mRequestorID = new ArrayList<>();

    public RecyclerViewApproverAdapter(Context mContext, ArrayList<String> mAssetType, ArrayList<String> mAssetNumber, ArrayList<String> mRequestedBy, ArrayList<String> mIsAcceptable, ArrayList<String> mRequestorID) {
        this.mAssetType = mAssetType;
        this.mAssetNumber = mAssetNumber;
        this.mRequestedBy = mRequestedBy;
        this.mIsAcceptable = mIsAcceptable;
        this.mContext = mContext;
        this.mRequestorID = mRequestorID;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_listitem, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        Log.d(TAG, "onBindViewHolder: called.");
        viewHolder.assetType.setText(mAssetType.get(i));
        viewHolder.assetNumber.setText(mAssetNumber.get(i));
        viewHolder.requestedBy.setText(mRequestedBy.get(i));
        viewHolder.isAcceptable.setBackgroundColor(Color.parseColor(mIsAcceptable.get(i)));
        viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked on: " + mAssetType.get(viewHolder.getAdapterPosition()));
                PopupMenu popupMenu = new PopupMenu(mContext, viewHolder.parentLayout);
                popupMenu.inflate(R.menu.menu_approver);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.approve:
                                Toast.makeText(mContext, "Approved " + mRequestorID.get(viewHolder.getAdapterPosition()), Toast.LENGTH_SHORT).show();
                                db.collection("users")
                                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot documentSnapshot = task.getResult();
                                                    final String admin_id = documentSnapshot.get("admin_id").toString();
                                                    db.collection("users")
                                                            .document(admin_id)
                                                            .get()
                                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        DocumentSnapshot documentSnapshot1 = task.getResult();
                                                                        Map<String, Object> requests = (Map<String, Object>) documentSnapshot1.get("requests");
                                                                        Log.e(TAG, "onComplete: requests is: " + requests.toString());
                                                                        Log.e(TAG, "onComplete: requests key: " + requests.keySet() + " requests value: " + requests.values());
                                                                        for (final Map.Entry<String, Object> entry : requests.entrySet()) {
                                                                            Log.d(TAG, "Hello world  " + entry.getKey() + "/" + entry.getValue());
                                                                            if (entry.getKey().equals(mRequestorID.get(viewHolder.getAdapterPosition()))) {
                                                                                Map<String, Object> ent = (Map<String, Object>) entry.getValue();
                                                                                for (final Map.Entry<String, Object> entr : ent.entrySet()) {
                                                                                    Log.d(TAG, "onComplete: hello world again " + entr.getKey() + "-" + entr.getValue());
                                                                                    Log.e(TAG, "onComplete: check result :" + documentSnapshot1.getReference().getId());

                                                                                    Log.e(TAG, "onComplete: check result :" + mAssetType.get(viewHolder.getAdapterPosition()));
                                                                                    Log.e(TAG, "onComplete: check result :" + documentSnapshot1.getReference().getFirestore().toString());
                                                                                    String query = "requests." + mRequestorID.get(viewHolder.getAdapterPosition()) + "." + mAssetType.get(viewHolder.getAdapterPosition()).toLowerCase() + "." + "approved";
                                                                                    Log.e(TAG, "onComplete: query is " + query);
                                                                                    documentSnapshot1
                                                                                            .getReference()
                                                                                            .update(
                                                                                                    query, "true"
                                                                                            );
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            });
                                                } else {
                                                    Log.e(TAG, "onComplete: task is unsuccessful " + task.getException().toString());
                                                }
                                            }
                                        });
                                break;
                            case R.id.decline:
                                Toast.makeText(mContext, "Declined", Toast.LENGTH_SHORT).show();
                                db.collection("users")
                                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot documentSnapshot = task.getResult();
                                                    final String admin_id = documentSnapshot.get("admin_id").toString();
                                                    db.collection("users")
                                                            .document(admin_id)
                                                            .get()
                                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        DocumentSnapshot documentSnapshot1 = task.getResult();
                                                                        Map<String, Object> requests = (Map<String, Object>) documentSnapshot1.get("requests");
                                                                        Log.e(TAG, "onComplete: requests is: " + requests.toString());
                                                                        Log.e(TAG, "onComplete: requests key: " + requests.keySet() + " requests value: " + requests.values());
                                                                        for (final Map.Entry<String, Object> entry : requests.entrySet()) {
                                                                            Log.d(TAG, "Hello world  " + entry.getKey() + "/" + entry.getValue());
                                                                            if (entry.getKey().equals(mRequestorID.get(viewHolder.getAdapterPosition()))) {
                                                                                Map<String, Object> ent = (Map<String, Object>) entry.getValue();
                                                                                for (final Map.Entry<String, Object> entr : ent.entrySet()) {
                                                                                    Log.d(TAG, "onComplete: hello world again " + entr.getKey() + "-" + entr.getValue());
                                                                                    Log.e(TAG, "onComplete: check result :" + documentSnapshot1.getReference().getId());

                                                                                    Log.e(TAG, "onComplete: check result :" + mAssetType.get(viewHolder.getAdapterPosition()));
                                                                                    Log.e(TAG, "onComplete: check result :" + documentSnapshot1.getReference().getFirestore().toString());
                                                                                    String query = "requests." + mRequestorID.get(viewHolder.getAdapterPosition()) + "." + mAssetType.get(viewHolder.getAdapterPosition()).toLowerCase() + "." + "approved";
                                                                                    Log.e(TAG, "onComplete: query is " + query);
                                                                                    documentSnapshot1
                                                                                            .getReference()
                                                                                            .update(
                                                                                                    query, "false"
                                                                                            );
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            });
                                                } else {
                                                    Log.e(TAG, "onComplete: task is unsuccessful " + task.getException().toString());
                                                }
                                            }
                                        });

                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAssetType.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView assetType, assetNumber, requestedBy, isAcceptable;
        RelativeLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            assetType = itemView.findViewById(R.id.asset_type);
            assetNumber = itemView.findViewById(R.id.asset_number);
            requestedBy = itemView.findViewById(R.id.requested_by);
            isAcceptable = itemView.findViewById(R.id.is_acceptable);
            parentLayout = itemView.findViewById(R.id.parent_layout_requested);
        }
    }
}
