package com.prince.assetManagement;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ai.api.android.AIDataService.TAG;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {
    private int RC_SIGN_IN = 0;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
            startActivity(intent);
        } else {
            // Choose authentication providers
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build(),
                    new AuthUI.IdpConfig.PhoneBuilder().build(),
                    new AuthUI.IdpConfig.GoogleBuilder().build());
// Create and launch sign-in intent
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.e(TAG, "onComplete: this query is starting");
                        if (task.isSuccessful()) {
                            int counter = 0;
                            Log.e(TAG, "onComplete: this is size" + task.getResult());
                            Log.e(TAG, "onComplete: this is size documents" + task.getResult().getDocuments());
                            Log.e(TAG, "onComplete: this is size documents" + task.getResult().getDocuments().size());
                            Log.e(TAG, "onComplete: this is size documents" + task.getResult().isEmpty());
                            if (!task.getResult().isEmpty()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    counter += 1;
                                    Log.d(TAG, "onComplete: " + document.getId() + "Data" + document.getData());
                                    Log.d(TAG, "onComplete: current user id " + FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    if (document.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                        Log.d(TAG, "onComplete: user already exits");
                                        Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Log.e(TAG, "onComplete: counter size outside loop" + counter );
                                        Log.e(TAG, "onComplete: task.size outside loop" + task.getResult().size() );
                                        if (counter == task.getResult().size()) {
                                            Log.e(TAG, "onComplete: counter size" + counter );
                                            Log.e(TAG, "onComplete: task.size inside loop" + task.getResult().size() );
                                            Log.d(TAG, "onComplete: new user");
                                            final Map<String, Object> user = new HashMap<>();
                                            user.put("name", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                                            user.put("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                            user.put("role", "admin");
                                            user.put("normal_users", Arrays.asList());
                                            user.put("approver", Arrays.asList());
                                            Log.e(TAG, "onComplete: isEmpty is not empty and new user");
                                            db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .set(user)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.e(TAG, "onSuccess: Data added successfully");
                                                            Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
                                                            startActivity(intent);
                                                        }
                                                    });
//                                            Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
//                                            startActivity(intent);
                                        }
                                    }
                                }
                            } else {
                                final Map<String, Object> user = new HashMap<>();
                                user.put("name", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                                user.put("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                user.put("role", "admin");
                                user.put("normal_users", Arrays.asList());
                                user.put("approver", Arrays.asList());
                                Log.e(TAG, "onComplete: isEmpty is true");
                                db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .set(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.e(TAG, "onSuccess: Data added successfully");
                                                Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
                                                startActivity(intent);
                                            }
                                        });

                            }
//                            Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
//                            startActivity(intent);
                        } else {
                            Log.d(TAG, "onComplete: this is the error" + task.getException());
                        }
                    }
                });
                // Successfully signed in
//                final String fullName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
//                Log.d(TAG, "onActivityResult: UserName " + fullName);

//                Intent intent = new Intent(getApplicationContext(), DetectorActivity.class);
//                startActivity(intent);
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }
}

