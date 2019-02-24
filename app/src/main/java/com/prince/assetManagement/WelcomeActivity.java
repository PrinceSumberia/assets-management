package com.prince.assetManagement;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.JsonElement;

import java.util.Map;

import ai.api.AIListener;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;


public class WelcomeActivity extends AppCompatActivity implements AIListener {
    FloatingActionButton listenButton;
    TextView resultTextView, logout;
    Button addAsset, scanAsset, serachAsset, addUsers, reported_assets, asset_requests, delete_assets;
    AIService aiService;
    private TextToSpeech textToSpeech;
    final Handler handler = new Handler();
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Getting Ids of the elements
        listenButton = findViewById(R.id.listen);
        resultTextView = findViewById(R.id.resultTextView);
        addAsset = findViewById(R.id.add_asset);
        scanAsset = findViewById(R.id.scan_asset);
        logout = findViewById(R.id.logout);
        serachAsset = findViewById(R.id.search_asset);
        addUsers = findViewById(R.id.add_users);
        reported_assets = findViewById(R.id.reported_assets);
        delete_assets = findViewById(R.id.delete_assets);
        asset_requests = findViewById(R.id.asset_requests);

        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);


        // Adding click listener on Scan and Add Button
        scanAsset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ScanActivity.class);
                startActivity(intent);
            }
        });
        addAsset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    Toast.makeText(WelcomeActivity.this, "You are not logged in", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(WelcomeActivity.this, "Your are logged in", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), DetectorActivity.class);
                    startActivity(intent);
                }
            }
        });
        serachAsset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    Toast.makeText(WelcomeActivity.this, "You are not logged in", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(WelcomeActivity.this, "Your are logged in", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), GetAssetInfo.class);
                    startActivity(intent);
                }
            }
        });

        addUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddUsers.class);
                intent.putExtra("admin_user", FirebaseAuth.getInstance().getCurrentUser().getUid());
                startActivity(intent);
            }
        });

        reported_assets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Intent intent = new Intent(getApplicationContext(), ReportedAssets.class);
                startActivity(intent);
            }
        });

        asset_requests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AdminAssetRequest.class);
                startActivity(intent);
            }
        });

        delete_assets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    Toast.makeText(WelcomeActivity.this, "You are not logged in", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(WelcomeActivity.this, "Your are logged in", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), DeleteAssets.class);
                    startActivity(intent);
                }
            }
        });

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseAuth.getInstance().signOut();
                    logout.setAlpha(0.0f);
                    Toast.makeText(WelcomeActivity.this, "Successfully Logged You Out", Toast.LENGTH_SHORT).show();
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

        // Setting up DialogFlow API
        final AIConfiguration config = new AIConfiguration("2ec31874df2141fdaae5db34bd43c1fb",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);
        aiService = AIService.getService(this, config);
        aiService.setListener(this);

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
            }
        });
    }

    public void listenButtonOnClick(final View view) {
        aiService.startListening();
    }

    @Override
    public void onResult(AIResponse response) {
        Result result = response.getResult();

        // Get parameters
        String parameterString = "";
        if (result.getParameters() != null && !result.getParameters().isEmpty()) {
            for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {
                parameterString += "(" + entry.getKey() + ", " + entry.getValue() + ") ";
            }
        }

        // Show results in TextView.
//        resultTextView.setText("Query:" + result.getResolvedQuery() +
//                "\nAction: " + result.getAction() +
//                "\nParameters: " + parameterString);
        String speech = result.getFulfillment().getSpeech();
        resultTextView.setText(speech);
        textToSpeech.speak(speech, TextToSpeech.QUEUE_FLUSH, null);

        if (result.getAction().equals("input.welcome")) {
            final String welcomeText = "Do You Want to Add an Asset or Scan an Asset";
            resultTextView.setText(welcomeText);
        }


        if (result.getResolvedQuery().contains("add")) {
            final Intent intent = new Intent(getApplicationContext(), DetectorActivity.class);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    startActivity(intent);
                }
            }, 1500);

        } else if (result.getResolvedQuery().contains("scan")) {
            final Intent intent = new Intent(getApplicationContext(), ScanActivity.class);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    startActivity(intent);
                }
            }, 2000);

        }
    }

    @Override
    public void onError(AIError error) {
        resultTextView.setText(error.toString());
    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {

    }

    @Override
    public void onListeningCanceled() {

    }

    @Override
    public void onListeningFinished() {

    }
}

