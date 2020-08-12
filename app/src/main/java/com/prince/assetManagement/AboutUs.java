package com.prince.assetManagement;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class AboutUs extends AppCompatActivity {
    Button features, credits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        features = findViewById(R.id.features);
        credits = findViewById(R.id.credits);

        features.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(AboutUs.this)
                        .setTitle("Features")
                        .setMessage("iAsset is a low-cost solution which works on low-cost devices. It automatically recognizes and categorizes assets using artificial intelligence, thus saving time. With iAsset, adding an asset to an organizations’ inventory takes less than a minute.\n\n" + "Features:\n" +
                                "•\tAutomatic Object Detection\n" +
                                "•\tAssigned Asset-User Security Scheme. \t\n" +
                                "•\tDynamic Bulk QR Code Generation\n" +
                                "•\tAuto Generation & Assignment of IDs.\n" +
                                "•\tBulk Geotag Assets using GPS.\n" +
                                "•\tRequesting and Reporting Asset.\n" +
                                "•\tAnalytics\n" +
                                "•\tData Security & User Authentication\n")
                        .setNegativeButton("Close", null)
                        .show();
            }
        });

        credits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(AboutUs.this)
                        .setTitle("Credits")
                        .setMessage(
                                "•\tOpen Source Community\n")
                        .setNegativeButton("Close", null)
                        .show();

            }
        });
    }
}
