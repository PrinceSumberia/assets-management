package com.prince.assetManagement;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";
    private ArrayList<String> mAssetType = new ArrayList<>();
    private ArrayList<String> mAssetStatus = new ArrayList<>();
    private ArrayList<String> mReportedBy = new ArrayList<>();
    private Context mContext;

    public RecyclerViewAdapter(Context mContext, ArrayList<String> mAssetType, ArrayList<String> mAssetStatus, ArrayList<String> mReportedBy) {
        this.mAssetType = mAssetType;
        this.mAssetStatus = mAssetStatus;
        this.mReportedBy = mReportedBy;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_listreports, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        Log.d(TAG, "onBindViewHolder: called.");
        viewHolder.assetType.setText(mAssetType.get(i));
        viewHolder.assetStatus.setText(mAssetStatus.get(i));
        viewHolder.reportedBy.setText(mReportedBy.get(i));
        viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked on: " + mAssetType.get(viewHolder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAssetType.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView assetType, assetStatus, reportedBy;
        RelativeLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            assetType = itemView.findViewById(R.id.asset_type);
            assetStatus = itemView.findViewById(R.id.asset_status);
            reportedBy = itemView.findViewById(R.id.reported_by);
            parentLayout = itemView.findViewById(R.id.parent_layout_reported);
        }
    }
}
