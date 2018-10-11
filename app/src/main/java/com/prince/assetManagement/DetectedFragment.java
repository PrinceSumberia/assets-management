package com.prince.assetManagement;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.prince.assetManagement.R;
import com.prince.assetManagement.env.Logger;


public class DetectedFragment extends Fragment {
    private static final Logger LOGGER = new Logger();
    TextView textView;
    private TextToSpeech textToSpeech;
    Button next;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view  = inflater.inflate(R.layout.fragment_detected, container, false);
        textView = view.findViewById(R.id.objectName);
        next = view.findViewById(R.id.next);
        final String detectedObject;
        detectedObject = getArguments().getString("detectedObject");

        textView.setText(detectedObject);
        this.textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
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
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new Date();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.fragment_area,fragment);
                ft.commit();
//                Intent myIntent = new Intent(getContext(), WelcomeActivity.class);
//                startActivity(myIntent);
                Toast.makeText(getContext(),"Switching Fragments",Toast.LENGTH_LONG).show();
            }
        });
        return view;
    }
}
