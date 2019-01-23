package com.prince.assetManagement;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RequestAsset extends AppCompatActivity {
    EditText assetType, assetNumber;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_asset);

        assetType = findViewById(R.id.asset_type);
        assetNumber = findViewById(R.id.asset_no);
        submit = findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type = assetType.getText().toString();
                String number = assetNumber.getText().toString();
                ReportAsset("princesumberia7@gmail.com", type, number);
            }
        });
    }

    public void ReportAsset(final String adminEmail, final String type, final String number) {
        new Thread(new Runnable() {

            public void run() {

                try {

                    GMailSender sender = new GMailSender(
                            "noreply.assetmanagement@gmail.com",
                            "PASSWORDPRINCE");
//                    sender.addAttachment(Environment.getExternalStorageDirectory().getPath() + "/image.jpg");

                    sender.sendMail("Asset Reported", " "+number+" asset "+ type + "has been requested by the user. Please login to your admin dashboard",

                            "noreply.assetmanagement@gmail.com", adminEmail);

                    Toast.makeText(RequestAsset.this, "Email Sent", Toast.LENGTH_SHORT).show();


                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                }
            }
        }).start();
    }
}
