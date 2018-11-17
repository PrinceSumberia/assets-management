package com.prince.assetManagement;


import android.app.Activity;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView.OnQRCodeReadListener;


public class ScanActivity extends Activity implements OnQRCodeReadListener {

    TextView resultTextView;
    QRCodeReaderView qrCodeReaderView;
    CheckBox flashlight, decodeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        flashlight = findViewById(R.id.flashlight_checkbox);
        decodeBtn = findViewById(R.id.enable_decoding_checkbox);

        resultTextView = findViewById(R.id.resultTextView);

        qrCodeReaderView = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
        qrCodeReaderView.setOnQRCodeReadListener(this);

        flashlight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    qrCodeReaderView.setTorchEnabled(true);
                }
                else {
                    qrCodeReaderView.setTorchEnabled(false);
                }
            }
        });

        decodeBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    qrCodeReaderView.setQRDecodingEnabled(true);
                }
                else {
                    qrCodeReaderView.setQRDecodingEnabled(false);
                    resultTextView.setText("");
                }
            }
        });
        // Use this function to enable/disable decoding

        // Use this function to change the autofocus interval (default is 5 secs)
        qrCodeReaderView.setAutofocusInterval(2000L);

        // Use this function to enable/disable Torch

        // Use this function to set front camera preview
        qrCodeReaderView.setFrontCamera();

        // Use this function to set back camera preview
        qrCodeReaderView.setBackCamera();
    }

    // Called when a QR is decoded
    // "text" : the text encoded in QR
    // "points" : points where QR control points are placed in View
    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        resultTextView.setText(text);
        Intent intent = new Intent(ScanActivity.this, GetInformation.class);
        intent.putExtra("qrcode_id", text);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        qrCodeReaderView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        qrCodeReaderView.stopCamera();
    }
}