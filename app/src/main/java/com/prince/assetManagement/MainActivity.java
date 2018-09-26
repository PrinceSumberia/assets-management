package com.prince.assetManagement;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.prince.assetManagement.env.Logger;

public class MainActivity extends AppCompatActivity {
    private static final Logger LOGGER = new Logger();
    TextView textView;
    private TextToSpeech textToSpeech;
    Button test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.objectName);
        test = findViewById(R.id.testing);



        Intent intent = getIntent();
        final String detectedObject = intent.getStringExtra("Detected Object");
        textView.setText(detectedObject);
        this.textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    StringBuilder stringBuilder = new StringBuilder();

                    stringBuilder.append(detectedObject);
                    stringBuilder.append(" detected.");
                    Log.e("What is ", "Line is " + stringBuilder.toString());

                    textToSpeech.speak(stringBuilder.toString(), TextToSpeech.QUEUE_FLUSH, null);
                    LOGGER.i("onCreate", "TextToSpeech is initialised");
                } else {
                    LOGGER.e("onCreate", "Cannot initialise text to speech!");
                }
            }
        });
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getApplicationContext(), WelcomeActivity.class);
                startActivity(myIntent);
                Toast.makeText(getApplicationContext(),"Switching Activities",Toast.LENGTH_LONG).show();
            }
        });
    }
}
