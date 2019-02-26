package com.prince.assetManagement;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;


public class WelcomeActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = "WelcomeActivity";
    GridViewAdapter gridViewAdapter;

    //WIDGETS
    GridView gridView;
    Integer[] img_id = {
            R.drawable.ic_add,
            R.drawable.ic_qr_code_final,
            R.drawable.ic_search,
            R.drawable.ic_employee,
            R.drawable.ic_wrench,
            R.drawable.ic_notification,
            R.drawable.ic_delete,
            R.drawable.ic_help,
            R.drawable.ic_sent,
    };

    String[] txt = {"Add Asset", "Scan Asset", "Search Asset", "Add Users", "Reported Assets", "Asset Requests",
            "Delete Assets", "Help", "Sign Out"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_welcome);
        gridView = findViewById(R.id.gridview);
        gridViewAdapter = new GridViewAdapter(this, txt, img_id);
        checkOrientation();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(WelcomeActivity.this, "Clicked " + i, Toast.LENGTH_SHORT).show();
                switch (i) {
                    case 0:
                        // Add Asset
                        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                            Toast.makeText(WelcomeActivity.this, "You are not logged in", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(WelcomeActivity.this, "Your are logged in", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), DetectorActivity.class);
                            startActivity(intent);
                        }
                        break;
                    case 1: {
                        Intent intent = new Intent(getApplicationContext(), ScanActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case 2:
                        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                            Toast.makeText(WelcomeActivity.this, "You are not logged in", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(WelcomeActivity.this, "Your are logged in", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), GetAssetInfo.class);
                            startActivity(intent);
                        }
                        break;
                    case 3: {
                        Intent intent = new Intent(getApplicationContext(), AddUsers.class);
                        intent.putExtra("admin_user", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        startActivity(intent);
                        break;
                    }
                    case 4: {
                        Intent intent = new Intent(getApplicationContext(), ReportedAssets.class);
                        startActivity(intent);
                        break;
                    }
                    case 5: {
                        Intent intent = new Intent(getApplicationContext(), AdminAssetRequest.class);
                        startActivity(intent);
                        break;
                    }
                    case 6:
                        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                            Toast.makeText(WelcomeActivity.this, "You are not logged in", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(WelcomeActivity.this, "Your are logged in", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), DeleteAssets.class);
                            startActivity(intent);
                        }
                        break;
                    case 7:
                        Toast.makeText(WelcomeActivity.this, "Ready to help", Toast.LENGTH_SHORT).show();
                        break;
                    case 8:
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(WelcomeActivity.this, "Successfully Logged You Out", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        break;
                }
            }
        });
        gridView.setAdapter(gridViewAdapter);
    }

    private void checkOrientation() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridView.setNumColumns(3);
        } else {
            gridView.setNumColumns(2);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}

