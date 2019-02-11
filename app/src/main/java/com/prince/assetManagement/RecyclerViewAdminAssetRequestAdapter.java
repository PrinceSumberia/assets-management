package com.prince.assetManagement;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class RecyclerViewAdminAssetRequestAdapter extends RecyclerView.Adapter<RecyclerViewAdminAssetRequestAdapter.ViewHolder> {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<String> mAssetType = new ArrayList<>();
    private ArrayList<String> mAssetNumber = new ArrayList<>();
    private ArrayList<String> mRequestedBy = new ArrayList<>();
    private ArrayList<String> mRequestorID = new ArrayList<>();
    private Context mContext;

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
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.assetType.setText(mAssetType.get(i));
        viewHolder.assetNumber.setText(mAssetNumber.get(i));
        viewHolder.requestedBy.setText(mRequestedBy.get(i));
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
