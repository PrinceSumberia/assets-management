package com.prince.assetManagement;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;

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
            db.collection("users")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                Log.e(TAG, "onComplete: First Task is successful");
                                DocumentSnapshot documentSnapshot = task.getResult();
                                String role = documentSnapshot.get("role").toString();
                                String admin_id = documentSnapshot.get("admin_id").toString();
                                String admin_email = documentSnapshot.get("admin_email").toString();
                                switch (role) {
                                    case "admin":
                                        if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                                            Log.e(TAG, "onComplete: User is admin first loop");
                                            Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                                            intent.putExtra("admin_id", admin_id);
                                            Log.e(TAG, "onComplete: login activity " + admin_id);
                                            intent.putExtra("admin_email", admin_email);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Please Verify Your Email To Sign in", Toast.LENGTH_SHORT).show();
                                            FirebaseAuth.getInstance().signOut();
                                            Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                                    Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        }
                                        break;
                                    case "approver": {
                                        if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                                            Log.e(TAG, "onComplete: User  is approver first loop");
                                            Intent intent = new Intent(getApplicationContext(), Approver.class);
                                            intent.putExtra("admin_id", admin_id);
                                            Log.e(TAG, "onComplete: login activity " + admin_id);
                                            intent.putExtra("admin_email", admin_email);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Please Verify Your Email To Sign in", Toast.LENGTH_SHORT).show();
                                            FirebaseAuth.getInstance().signOut();
                                            Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                                    Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        }
                                        break;
                                    }
                                    case "normal user": {
                                        if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                                            Log.e(TAG, "onComplete: User is normal user first loop");
                                            Intent intent = new Intent(getApplicationContext(), NormalUser.class);
                                            Log.e(TAG, "onComplete: login activity " + admin_id);
                                            intent.putExtra("admin_id", admin_id);
                                            intent.putExtra("admin_email", admin_email);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Please Verify Your Email To Sign in", Toast.LENGTH_SHORT).show();
                                            FirebaseAuth.getInstance().signOut();
                                            Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                                    Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        }
                                        break;
                                    }
                                    default: {
                                        Log.e(TAG, "onComplete: User is neither");
                                        Toast.makeText(LoginActivity.this, "Error Verifying Identity", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), NormalUser.class);
                                        startActivity(intent);
                                        break;
                                    }
                                }
                            }
                        }
                    });
//            Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
//            startActivity(intent);
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
                db.collection("users")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                                                switch (document.get("role").toString()) {
                                                    case "admin": {
                                                        if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                                                            counter += task.getResult().size();
                                                            Log.d(TAG, "onComplete: user already exits and role is admin " + document.get("role").toString());
                                                            String admin_id = document.get("admin_id").toString();
                                                            String admin_email = document.get("admin_email").toString();
                                                            Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                                                            intent.putExtra("admin_id", admin_id);
                                                            intent.putExtra("admin_email", admin_email);
                                                            startActivity(intent);
                                                        } else {
                                                            Toast.makeText(LoginActivity.this, "Please Verify Your Email To Sign in", Toast.LENGTH_SHORT).show();
                                                            FirebaseAuth.getInstance().signOut();
                                                            Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                                                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                                                    Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                        break;
                                                    }
                                                    case "normal user": {
                                                        if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                                                            counter += task.getResult().size();
                                                            Log.e(TAG, "onComplete: user is normal user" + document.get("role").toString());
                                                            Toast.makeText(LoginActivity.this, "User is normal user", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(getApplicationContext(), NormalUser.class);
                                                            String admin_id = document.get("admin_id").toString();
                                                            String admin_email = document.get("admin_email").toString();
                                                            intent.putExtra("admin_id", admin_id);
                                                            intent.putExtra("admin_email", admin_email);
                                                            startActivity(intent);
                                                        } else {
                                                            Toast.makeText(LoginActivity.this, "Please Verify Your Email To Sign in", Toast.LENGTH_SHORT).show();
                                                            FirebaseAuth.getInstance().signOut();
                                                            Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                                                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                                                    Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                        break;
                                                    }
                                                    case "approver": {
                                                        if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                                                            counter += task.getResult().size();
                                                            Log.e(TAG, "onComplete: user is a approver" + document.get("role").toString());
                                                            Toast.makeText(LoginActivity.this, "User is approver", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(getApplicationContext(), Approver.class);
                                                            String admin_id = document.get("admin_id").toString();
                                                            String admin_email = document.get("admin_email").toString();
                                                            intent.putExtra("admin_id", admin_id);
                                                            intent.putExtra("admin_email", admin_email);
                                                            startActivity(intent);
                                                        } else {
                                                            Toast.makeText(LoginActivity.this, "Please Verify Your Email To Sign in", Toast.LENGTH_SHORT).show();
                                                            FirebaseAuth.getInstance().signOut();
                                                            Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                                                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                                                    Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                        break;
                                                    }
                                                    default: {
                                                        counter += task.getResult().size();
                                                        Log.e(TAG, "onComplete: User is neither approver, admin or normal user" + document.get("role").toString());
                                                        Toast.makeText(LoginActivity.this, "Role is not defined", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                                                        startActivity(intent);
                                                        break;
                                                    }
                                                }
                                            } else {
                                                Log.e(TAG, "onComplete: counter size outside loop" + counter);
                                                Log.e(TAG, "onComplete: task.size outside loop" + task.getResult().size());
                                                if (counter == task.getResult().size()) {
                                                    Log.e(TAG, "onComplete: counter size" + counter);
                                                    Log.e(TAG, "onComplete: task.size inside loop" + task.getResult().size());
                                                    Log.d(TAG, "onComplete: new user");
                                                    final Map<String, Object> user = new HashMap<>();
                                                    user.put("name", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                                                    user.put("admin_email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                                    user.put("role", "admin");
                                                    user.put("admin_id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                    ArrayList<Integer> year_list = new ArrayList<>();
//                                            int year = Calendar.getInstance().get(Calendar.YEAR);
//                                            Log.d(TAG, "onComplete: the current year is " + year);
                                                    for (int i = 2000; i <= 2050; i++) {
                                                        year_list.add(i);
                                                    }
                                                    user.put("year", year_list);
                                                    user.put("normal_users", Arrays.asList());
                                                    user.put("approver", Arrays.asList());
                                                    Log.e(TAG, "onComplete: isEmpty is not empty and new user");
                                                    final Map<String, Object> admin_list = new HashMap<>();
                                                    admin_list.put(FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                    user.put("admin_users", admin_list);
                                                    final Map<String, Object> assets_label = new HashMap<>();
                                                    user.put("assets_label", assets_label);
                                                    db.collection("users")
                                                            .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                            .set(user)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Log.e(TAG, "onSuccess: Data added successfully");
                                                                    Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                                                                    FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();
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
                                        user.put("admin_email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                        user.put("role", "admin");
                                        user.put("admin_id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        ArrayList<Integer> year_list = new ArrayList<>();
//                                int year = Calendar.getInstance().get(Calendar.YEAR);
//                                Log.d(TAG, "onComplete: the current year is " + year);
                                        for (int i = 2000; i <= 2050; i++) {
                                            year_list.add(i);
                                        }
                                        user.put("year", year_list);
                                        user.put("normal_users", Arrays.asList());
                                        user.put("approver", Arrays.asList());
//                                        final Map<String, Object> admin_users = new HashMap<>();
//                                        admin_users.put("admin_users", admin_list);
                                        final Map<String, Object> admin_list = new HashMap<>();
                                        admin_list.put(FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        user.put("admin_users", admin_list);
                                        final Map<String, Object> assets_label = new HashMap<>();
                                        user.put("assets_label", assets_label);
                                        Log.e(TAG, "onComplete: isEmpty is true");
                                        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .set(user)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.e(TAG, "onSuccess: Data added successfully");
                                                        Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                                                        FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();
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

