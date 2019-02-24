package com.prince.assetManagement;

import android.content.Context;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewAdminAssetRequestAdapter extends RecyclerView.Adapter<RecyclerViewAdminAssetRequestAdapter.ViewHolder> {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<String> mAssetType = new ArrayList<>();
    private ArrayList<String> mAssetNumber = new ArrayList<>();
    private ArrayList<String> mRequestedBy = new ArrayList<>();
    private ArrayList<String> mRequestorID = new ArrayList<>();
    private static final String TAG = "RecyclerViewAdminAssetR";
    private Context mContext;
    String user_email;

    public RecyclerViewAdminAssetRequestAdapter(Context mContext, ArrayList<String> mAssetType, ArrayList<String> mAssetNumber, ArrayList<String> mRequestedBy, ArrayList<String> mRequestorID) {
        this.mAssetType = mAssetType;
        this.mAssetNumber = mAssetNumber;
        this.mRequestedBy = mRequestedBy;
        this.mContext = mContext;
        this.mRequestorID = mRequestorID;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_listadminassets, viewGroup, false);
        RecyclerViewAdminAssetRequestAdapter.ViewHolder holder = new RecyclerViewAdminAssetRequestAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        viewHolder.assetType.setText(mAssetType.get(i));
        viewHolder.assetNumber.setText(mAssetNumber.get(i));
        viewHolder.requestedBy.setText(mRequestedBy.get(i));
        viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked on: " + mAssetType.get(viewHolder.getAdapterPosition()));
                PopupMenu popupMenu = new PopupMenu(mContext, viewHolder.parentLayout);
                popupMenu.inflate(R.menu.menu_admin_approve);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.issued) {
                            Toast.makeText(mContext, "Issued" + mRequestorID.get(viewHolder.getAdapterPosition()), Toast.LENGTH_SHORT).show();

                            db.collection("users")
                                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                String date = null;
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
                                                            String query = "requests." + mRequestorID.get(viewHolder.getAdapterPosition()) + "." + mAssetType.get(viewHolder.getAdapterPosition()).toLowerCase();
                                                            Log.e(TAG, "onComplete: query is " + query);
                                                            date = ((Map<String, String>) entr.getValue()).get("date");
                                                            user_email = ((Map<String, String>) entr.getValue()).get("user_email");
                                                            documentSnapshot1.getReference()
                                                                    .update(
                                                                            query, FieldValue.delete()
                                                                    );
                                                        }
                                                    }
                                                }
                                                String subject = "Asset Request Approved";
                                                String status = "approved";
                                                String name = mRequestedBy.get(viewHolder.getAdapterPosition());
                                                String asset = mAssetType.get(viewHolder.getAdapterPosition());
                                                String number = mAssetNumber.get(viewHolder.getAdapterPosition());
//                                                                    mailStatus(subject, status, date, user_email, asset, number);
                                            }
                                        }
                                    });

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
        TextView assetType, assetNumber, requestedBy;
        RelativeLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            assetType = itemView.findViewById(R.id.asset_type_admin);
            assetNumber = itemView.findViewById(R.id.asset_number_admin);
            requestedBy = itemView.findViewById(R.id.requested_by_admin);
            parentLayout = itemView.findViewById(R.id.parent_layout_admin_asset_request);
        }
    }
}
