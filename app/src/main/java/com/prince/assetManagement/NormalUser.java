package com.prince.assetManagement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class NormalUser extends AppCompatActivity {

    Button requestAsset, reportAsset, scanAsset;
    TextView logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_user);
        requestAsset = findViewById(R.id.request_asset);
//        reportAsset = findViewById(R.id.report_asset);
        scanAsset = findViewById(R.id.scan_asset_user);
        logout = findViewById(R.id.logout_user);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseAuth.getInstance().signOut();
                    logout.setAlpha(0.0f);
                    Toast.makeText(getApplicationContext(), "Successfully Logged You Out", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            });
        } else {
            logout.setAlpha(0.0f);
        }

        scanAsset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ScanActivity.class);
                startActivity(intent);
            }
        });


        requestAsset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RequestAsset.class);
                startActivity(intent);
                Toast.makeText(NormalUser.this, "Request Asset", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
