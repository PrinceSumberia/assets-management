package com.prince.assetManagement;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Credentials;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.CredentialsClient;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.prince.assetManagement.R;
import com.prince.assetManagement.env.Logger;

import java.util.HashMap;
import java.util.Map;

import static ai.api.android.AIDataService.TAG;


public class DetectedFragment extends Fragment {
    private static final Logger LOGGER = new Logger();
    TextView textView, logout;
    private TextToSpeech textToSpeech;
    Button next;
    private FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    GoogleApiClient mGoogleApiClient;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view  = inflater.inflate(R.layout.fragment_detected, container, false);
        textView = view.findViewById(R.id.objectName);
        next = view.findViewById(R.id.next);
        logout = view.findViewById(R.id.logout);
        final String detectedObject;
        detectedObject = getArguments().getString("detectedObject");
        mAuth = FirebaseAuth.getInstance();

        textView.setText(detectedObject);
        final Map<String, Object> asset = new HashMap<>();
        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        asset.put("category", detectedObject);
        db.collection("users").document(userid).collection("assets").add(asset)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                     }
                });
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
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
//                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
//                mGoogleSignInClient.signOut().addOnCompleteListener(getActivity(),
//                        new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                            }
//                        });
                Toast.makeText(getContext(), "Logout Successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), WelcomeActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}
