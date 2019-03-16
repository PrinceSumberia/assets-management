package com.prince.assetManagement;

import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class DeleteAssetInfo extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_asset_info);
        String selected_item = getIntent().getStringExtra("Selected Item");
        Toast.makeText(this, "Selected Item is: " + selected_item, Toast.LENGTH_SHORT).show();

        String admin_id = getIntent().getStringExtra("admin_id");
        String admin_email = getIntent().getStringExtra("admin_email");

        if (selected_item.equals("By Year")) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment fragment = new DeleteFragmentDepartment();
            Bundle b = new Bundle();
            b.putString("admin_id", admin_id);
            b.putString("admin_email", admin_email);
            fragment.setArguments(b);
            fragmentTransaction.replace(R.id.fragment_area, fragment);
            fragmentTransaction.commit();
        } else if (selected_item.equals("By Asset Type")) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment fragment = new DeleteFragmentAssetType();
            Bundle b = new Bundle();
            b.putString("admin_id", admin_id);
            b.putString("admin_email", admin_email);
            fragment.setArguments(b);
            fragmentTransaction.replace(R.id.fragment_area, fragment);
            fragmentTransaction.commit();
        }
    }
}
