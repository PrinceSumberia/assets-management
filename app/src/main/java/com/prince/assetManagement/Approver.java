package com.prince.assetManagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;

public class Approver extends AppCompatActivity {

    Button scanAsset, approveRequest;
    TextView logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approver);
        scanAsset = findViewById(R.id.scan_asset_approver);
        approveRequest = findViewById(R.id.approve_asset);
        logout = findViewById(R.id.logout_approver);

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

        approveRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ApproverAssetRequest.class);
                startActivity(intent);
                Toast.makeText(Approver.this, "Approve Request", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
