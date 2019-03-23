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

public class IssuedAssetsAdapter extends RecyclerView.Adapter<IssuedAssetsAdapter.ViewHolder> {
    private static final String TAG = "IssuedAssetsAdapter";

    private Context mContext;
    private ArrayList<String> mAssetType = new ArrayList<>();
    private ArrayList<String> mAssetNumber = new ArrayList<>();
    private ArrayList<String> mIssuedDate = new ArrayList<>();

    public IssuedAssetsAdapter(Context mContext, ArrayList<String> mAssetType, ArrayList<String> mAssetNumber, ArrayList<String> mIssuedDate) {
        this.mContext = mContext;
        this.mAssetType = mAssetType;
        this.mAssetNumber = mAssetNumber;
        this.mIssuedDate = mIssuedDate;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.issued_assets_list, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");
        viewHolder.assetType.setText(mAssetType.get(position));
        viewHolder.assetNumber.setText(mAssetNumber.get(position));
        viewHolder.issuedDate.setText(mIssuedDate.get(position));
        viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked on " + mAssetType.get(viewHolder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAssetType.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView assetType, assetNumber, issuedDate;
        RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            assetType = itemView.findViewById(R.id.asset_type);
            assetNumber = itemView.findViewById(R.id.asset_number);
            issuedDate = itemView.findViewById(R.id.issued_date);
            parentLayout = itemView.findViewById(R.id.parent_layout_issued_assets);
        }
    }
}
