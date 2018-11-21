package com.prince.assetManagement;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

public class Display_Info extends FragmentActivity {

    String asset;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String TAG = "Display Info";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display__info);
        String selected_item = getIntent().getStringExtra("Selected Item");
        Toast.makeText(this, "Selected Item is: " + selected_item, Toast.LENGTH_SHORT).show();

        if (selected_item.equals("By Department")) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment fragment = new FragmentDepartment();

            fragmentTransaction.replace(R.id.fragment_area, fragment);
            fragmentTransaction.commit();
        }
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
