package com.prince.assetManagement;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

import static java.util.Arrays.sort;

public class Display_Graph extends FragmentActivity {
    private static final String TAG = "Class";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display__graph);
        String graph = getIntent().getStringExtra("graph");

//        String[] arr = getIntent().getStringArrayExtra("data");
//        ArrayList<String> arr_list = getIntent().getStringArray("data");
//        ArrayList<String> list_year = getIntent().getStringArrayListExtra("data");
//        String[] list_year_new = getIntent().getStringArrayListExtra("data");
//        Log.e(TAG, "onCreate: array in display graph " + list_year.toString() );
        if (graph.equals("year")){
            String[] stringArray = getIntent().getStringArrayListExtra("data").toArray(new String[0]);
            Log.e(TAG, "onCreate: this is it" + stringArray );
            sort(stringArray);
            Bundle bundle = new Bundle();
            bundle.putStringArray("data",stringArray);
//            bundle.putStringArrayList("data", list_year);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment fragment = new FragmentGraphYear();
            fragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.fragment_area, fragment);
            fragmentTransaction.commit();
        }
        if (graph.equals("value")){
            Bundle bundle = new Bundle();
            String[] valueArray = getIntent().getStringArrayListExtra("valueData").toArray(new String[0]);
            String[] assetsArray = getIntent().getStringArrayListExtra("assetsData").toArray(new String[0]);
            bundle.putStringArray("valueData",valueArray);
            bundle.putStringArray("assetsData",assetsArray);
//            bundle.putStringArrayList("data", list_year);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment fragment = new FragmentGraphValue();
            fragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.fragment_area, fragment);
            fragmentTransaction.commit();
        }
    }
}
