package com.prince.assetManagement;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class Display_Info extends FragmentActivity {

    String asset;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String TAG = "Display Info";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display__info);
        String selected_item = getIntent().getStringExtra("Selected Item");
        String admin_id = getIntent().getStringExtra("admin_id");
        String admin_email = getIntent().getStringExtra("admin_email");
        Toast.makeText(this, "Selected Item is: " + selected_item, Toast.LENGTH_SHORT).show();

        if (selected_item.equals("By Department")) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment fragment = new FragmentDepartment();
            Bundle b = new Bundle();
            b.putString("admin_id", admin_id);
            b.putString("admin_email", admin_email);
            fragment.setArguments(b);
            fragmentTransaction.replace(R.id.fragment_area, fragment);
            fragmentTransaction.commit();
        } else if (selected_item.equals("By User")) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment fragment = new FragmentUser();
            Bundle b = new Bundle();
            b.putString("admin_id", admin_id);
            b.putString("admin_email", admin_email);
            fragment.setArguments(b);
            fragmentTransaction.replace(R.id.fragment_area, fragment);
            fragmentTransaction.commit();
        } else if (selected_item.equals("By Vendor")) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment fragment = new FragmentVendor();
            Bundle b = new Bundle();
            b.putString("admin_id", admin_id);
            b.putString("admin_email", admin_email);
            fragment.setArguments(b);
            fragmentTransaction.replace(R.id.fragment_area, fragment);
            fragmentTransaction.commit();
        } else if (selected_item.equals("By Year")) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment fragment = new FragmentDate();
            Bundle b = new Bundle();
            b.putString("admin_id", admin_id);
            b.putString("admin_email", admin_email);
            fragment.setArguments(b);
            fragmentTransaction.replace(R.id.fragment_area, fragment);
            fragmentTransaction.commit();
        } else if (selected_item.equals("By Asset Type")) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment fragment = new FragmentAssetType();
            Bundle b = new Bundle();
            b.putString("admin_id", admin_id);
            b.putString("admin_email", admin_email);
            fragment.setArguments(b);
            fragmentTransaction.replace(R.id.fragment_area, fragment);
            fragmentTransaction.commit();
        } else if (selected_item.equals("By Value")) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment fragment = new FragmentValue();
            Bundle b = new Bundle();
            b.putString("admin_id", admin_id);
            b.putString("admin_email", admin_email);
            fragment.setArguments(b);
            fragmentTransaction.replace(R.id.fragment_area, fragment);
            fragmentTransaction.commit();
        } else if (selected_item.equals("By Admin")) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment fragment = new FragmentAdmin();
            Bundle b = new Bundle();
            b.putString("admin_id", admin_id);
            b.putString("admin_email", admin_email);
            fragment.setArguments(b);
            fragmentTransaction.replace(R.id.fragment_area, fragment);
            fragmentTransaction.commit();
        }
// else if (selected_item.equals("By Admin")) {
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            Fragment fragment = new FragmentAdmin();
//            Bundle b = new Bundle();
//            b.putString("admin_id", admin_id);
//            b.putString("admin_email", admin_email);
//            fragment.setArguments(b);
//            fragmentTransaction.replace(R.id.fragment_area, fragment);
//            fragmentTransaction.commit();
//        }
//        Bundle bundle = getIntent().getExtras();
////        DocumentReference query = db.collection("users").document(u).collection(detectedObject);
//
//
//        String category = bundle.getString("category");
//        String room_name = bundle.getString("room");
//        String department = bundle.getString("department");
//        String issued_to = bundle.getString("issued_to");
//        String seller = bundle.getString("seller");

//        if (!(room_name.equals("null"))) {
//            query = query + ".whereEqualTo(" + "room" + "," + room_name + ")";
//            Log.e(TAG, "Query is " + query);

    }


}
