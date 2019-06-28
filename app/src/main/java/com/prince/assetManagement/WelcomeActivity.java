package com.prince.assetManagement;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.UUID;


public class WelcomeActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = "WelcomeActivity";
    private final int PICK_IMAGE_REQUEST = 71;
    GridViewAdapter gridViewAdapter;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar toolbar;
    private ActionBar actionBar;
    TextView userEmail, userName;
    ImageView editImage, avatar;
    Uri filePath;
    StorageReference storageReference;
    FirebaseStorage storage;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    //WIDGETS
    GridView gridView;
    Integer[] img_id = {
            R.drawable.ic_add,
            R.drawable.ic_qr_code_final,
            R.drawable.ic_search,
            R.drawable.ic_issue,
            R.drawable.ic_employee,
            R.drawable.ic_wrench,
            R.drawable.ic_notification,
            R.drawable.ic_delete,
            R.drawable.ic_info,
            R.drawable.ic_sent,
    };

    String[] txt = {"Add Asset", "Scan Asset", "Search Asset", "Issue Assets", "Add Users", "Reported Assets", "Asset Requests",
            "Delete Assets", "About", "Sign Out"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        gridView = findViewById(R.id.gridview);
        FirebaseApp.initializeApp(getApplicationContext());
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        initToolbar();
        initNavigationMenu();
        gridViewAdapter = new GridViewAdapter(this, txt, img_id);
        checkOrientation();
        final String admin_id = getIntent().getStringExtra("admin_id");
        final String admin_email = getIntent().getStringExtra("admin_email");

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(WelcomeActivity.this, "Clicked " + i, Toast.LENGTH_SHORT).show();
                switch (i) {
                    case 0:
                        // Add Asset
                        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                            Toast.makeText(WelcomeActivity.this, "You are not logged in", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                        } else {
//                            Toast.makeText(WelcomeActivity.this, "Your are logged in", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), DetectorActivity.class);
                            intent.putExtra("admin_id", admin_id);
                            intent.putExtra("admin_email", admin_email);
                            startActivity(intent);
                        }
                        break;
                    case 1: {
                        Intent intent = new Intent(getApplicationContext(), ScanActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case 2:
                        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                            Toast.makeText(WelcomeActivity.this, "You are not logged in", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                        } else {
//                            Toast.makeText(WelcomeActivity.this, "Your are logged in", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), GetAssetInfo.class);
                            intent.putExtra("admin_id", admin_id);
                            Log.e(TAG, "onItemClick: reported admin id" + admin_id);
                            Log.e(TAG, "onItemClick: reported admin email" + admin_email);
                            intent.putExtra("admin_email", admin_email);
                            startActivity(intent);
                        }
                        break;
                    case 3: {
                        Intent intent = new Intent(getApplicationContext(), IssueAssets.class);
                        intent.putExtra("admin_id", admin_id);
                        intent.putExtra("admin_email", admin_email);
                        startActivity(intent);
//                        Toast.makeText(WelcomeActivity.this, "Issuing assets clicked", Toast.LENGTH_SHORT).show();
                    }
                    break;
                    case 4: {
                        db.collection("users")
                                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot documentSnapshot = task.getResult();
                                            try {
                                                String admin_id = documentSnapshot.get("admin_id").toString();
                                                String admin_email = documentSnapshot.get("admin_email").toString();
                                                Intent intent = new Intent(getApplicationContext(), AddUsers.class);
                                                intent.putExtra("admin_id", admin_id);
                                                intent.putExtra("admin_email", admin_email);
                                                startActivity(intent);
                                            } catch (NullPointerException e) {
                                                Log.e(TAG, "onComplete: " + e.getMessage());
                                            }
                                        }
                                    }
                                });
                        break;
                    }
                    case 5: {
                        Intent intent = new Intent(getApplicationContext(), ReportedAssets.class);
                        intent.putExtra("admin_id", admin_id);
                        intent.putExtra("admin_email", admin_email);
                        startActivity(intent);
                        break;
                    }
                    case 6: {
                        Intent intent = new Intent(getApplicationContext(), AdminAssetRequest.class);
                        intent.putExtra("admin_id", admin_id);
                        intent.putExtra("admin_email", admin_email);
                        startActivity(intent);
                        break;
                    }
                    case 7:
                        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                            Toast.makeText(WelcomeActivity.this, "You are not logged in", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                        } else {
//                            Toast.makeText(WelcomeActivity.this, "Your are logged in", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), DeleteAssets.class);
                            intent.putExtra("admin_id", admin_id);
                            intent.putExtra("admin_email", admin_email);
                            startActivity(intent);
                        }
                        break;
                    case 8:
//                        Toast.makeText(WelcomeActivity.this, "About", Toast.LENGTH_SHORT).show();
                        Intent intent2 = new Intent(getApplicationContext(), AboutUs.class);
                        startActivity(intent2);
                        break;
                    case 9:
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(WelcomeActivity.this, "Successfully Logged You Out", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        break;
                }
            }
        });
        gridView.setAdapter(gridViewAdapter);
    }

    private void checkOrientation() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridView.setNumColumns(3);
        } else {
            gridView.setNumColumns(2);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("iAsset");
        setSystemBarColor(this, R.color.grey_5);
        setSystemBarLight(this);
    }

    public static void setSystemBarColor(Activity act, @ColorRes int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = act.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(act.getResources().getColor(color));
        }
    }

    public static void setSystemBarLight(Activity act) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View view = act.findViewById(android.R.id.content);
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        changeMenuIconColor(menu, getResources().getColor(R.color.grey_60));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(WelcomeActivity.this, "Successfully Logged You Out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), StartActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public static void changeMenuIconColor(Menu menu, @ColorInt int color) {
        for (int i = 0; i < menu.size(); i++) {
            Drawable drawable = menu.getItem(i).getIcon();
            if (drawable == null) continue;
            drawable.mutate();
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }
    }

    private void initNavigationMenu() {
        NavigationView nav_view = findViewById(R.id.nav_view);
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        View headerView = nav_view.getHeaderView(0);
        userEmail = headerView.findViewById(R.id.draw_user_email);
        userName = headerView.findViewById(R.id.draw_user_name);
        editImage = headerView.findViewById(R.id.edit);
        avatar = headerView.findViewById(R.id.avatar);
        Uri uri = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
        if (uri != null) {
            Log.e(TAG, "initNavigationMenu: url is second" + uri);
            Picasso.get().load(uri).into(avatar);
        }
        userEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());


        try {
            userName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName().toUpperCase());
        } catch (NullPointerException e) {
            userName.setText("");
            Log.e(TAG, "initNavigationMenu: Username is not defined");
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(final MenuItem item) {
                Toast.makeText(getApplicationContext(), item.getTitle() + " Selected", Toast.LENGTH_SHORT).show();
//                actionBar.setTitle(item.getTitle());
                if (item.getTitle().equals("About")) {
                    Intent intent = new Intent(getApplicationContext(), AboutUs.class);
                    startActivity(intent);
                } else if (item.getTitle().equals("Logout")) {
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(WelcomeActivity.this, "Successfully Logged You Out", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                drawer.closeDrawers();
                return true;
            }
        });

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(WelcomeActivity.this, "Working", Toast.LENGTH_SHORT).show();
                chooseImage();

            }
        });
        // open drawer at start
//        drawer.openDrawer(GravityCompat.START);
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(WelcomeActivity.this.getContentResolver(), filePath);
                uploadImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(WelcomeActivity.this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            final StorageReference ref = storageReference.child("/user_images/" + UUID.randomUUID().toString());

            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String image_url = uri.toString();
                                    Log.d(TAG, "onSuccess: url = " + uri.toString());
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setPhotoUri(uri)
                                            .build();

                                    user.updateProfile(profileUpdates)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d(TAG, "User profile updated.");
                                                        Uri uri = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
                                                        Picasso.get().load(uri).into(avatar);
                                                        Toast.makeText(WelcomeActivity.this, "Profile Photo Updated", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(WelcomeActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }
}

