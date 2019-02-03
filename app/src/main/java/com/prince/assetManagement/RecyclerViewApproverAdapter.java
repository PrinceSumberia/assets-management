package com.prince.assetManagement;

import android.annotation.SuppressLint;
import android.content.Context;
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

import java.util.ArrayList;

public class RecyclerViewApproverAdapter extends RecyclerView.Adapter<RecyclerViewApproverAdapter.ViewHolder> {
    private static final String TAG = "RecyclerViewApproverAdapter";
    private ArrayList<String> mAssetType = new ArrayList<>();
    private ArrayList<String> mAssetNumber = new ArrayList<>();
    private ArrayList<String> mRequestedBy = new ArrayList<>();
    private Context mContext;

    public RecyclerViewApproverAdapter(Context mContext, ArrayList<String> mAssetType, ArrayList<String> mAssetNumber, ArrayList<String> mRequestedBy) {
        this.mAssetType = mAssetType;
        this.mAssetNumber = mAssetNumber;
        this.mRequestedBy = mRequestedBy;
        this.mContext = mContext;
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
                                Toast.makeText(mContext, "Approved", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.decline:
                                Toast.makeText(mContext, "Declined", Toast.LENGTH_SHORT).show();
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
        TextView assetType, assetNumber, requestedBy;
        RelativeLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            assetType = itemView.findViewById(R.id.asset_type);
            assetNumber = itemView.findViewById(R.id.asset_number);
            requestedBy = itemView.findViewById(R.id.requested_by);
            parentLayout = itemView.findViewById(R.id.parent_layout_requested);
        }
    }
}
