package com.prince.assetManagement;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GetInfoList extends RecyclerView.Adapter<GetInfoList.ViewHolder> {
    private static final String TAG = "GetInfoList";
    private ArrayList<String> mAssetType = new ArrayList<>();
    private ArrayList<String> mAssetNumber = new ArrayList<>();
    private Context mContext;

    public GetInfoList(ArrayList<String> mAssetType, ArrayList<String> mAssetNumber, Context mContext) {
        this.mAssetType = mAssetType;
        this.mAssetNumber = mAssetNumber;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.get_info_list, parent, false);
        GetInfoList.ViewHolder holder = new GetInfoList.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.assetType.setText(mAssetType.get(position));
        holder.assetNumber.setText(mAssetNumber.get(position));
    }

    @Override
    public int getItemCount() {
        return mAssetType.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView assetType, assetNumber;
        RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            assetType = itemView.findViewById(R.id.asset_type);
            assetNumber = itemView.findViewById(R.id.asset_number);
            parentLayout = itemView.findViewById(R.id.parent_layout_get_info_list);
        }
    }
}
