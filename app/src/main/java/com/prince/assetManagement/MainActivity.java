package com.prince.assetManagement;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.prince.assetManagement.Unused.DetectedFragment;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String detectedObject = intent.getStringExtra("Detected Object");
        Bundle bundle = new Bundle();
        bundle.putString("detectedObject", detectedObject);
        fragment = new DetectedFragment();
        fragment.setArguments(bundle);
        Toast.makeText(getApplicationContext(), "Hello", Toast.LENGTH_LONG).show();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_area, fragment);
        ft.commit();
    }
}
