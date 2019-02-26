package com.prince.assetManagement;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;


public class WelcomeActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = "WelcomeActivity";
    GridViewAdapter gridViewAdapter;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar toolbar;
    private ActionBar actionBar;
    TextView userEmail, userName;

    //WIDGETS
    GridView gridView;
    Integer[] img_id = {
            R.drawable.ic_add,
            R.drawable.ic_qr_code_final,
            R.drawable.ic_search,
            R.drawable.ic_employee,
            R.drawable.ic_wrench,
            R.drawable.ic_notification,
            R.drawable.ic_delete,
            R.drawable.ic_help,
            R.drawable.ic_sent,
    };

    String[] txt = {"Add Asset", "Scan Asset", "Search Asset", "Add Users", "Reported Assets", "Asset Requests",
            "Delete Assets", "Help", "Sign Out"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        gridView = findViewById(R.id.gridview);
        initToolbar();
        initNavigationMenu();
        gridViewAdapter = new GridViewAdapter(this, txt, img_id);
        checkOrientation();
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
                            startActivity(intent);
                        }
                        break;
                    case 3: {
                        Intent intent = new Intent(getApplicationContext(), AddUsers.class);
                        intent.putExtra("admin_user", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        startActivity(intent);
                        break;
                    }
                    case 4: {
                        Intent intent = new Intent(getApplicationContext(), ReportedAssets.class);
                        startActivity(intent);
                        break;
                    }
                    case 5: {
                        Intent intent = new Intent(getApplicationContext(), AdminAssetRequest.class);
                        startActivity(intent);
                        break;
                    }
                    case 6:
                        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                            Toast.makeText(WelcomeActivity.this, "You are not logged in", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                        } else {
//                            Toast.makeText(WelcomeActivity.this, "Your are logged in", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), DeleteAssets.class);
                            startActivity(intent);
                        }
                        break;
                    case 7:
                        Toast.makeText(WelcomeActivity.this, "Ready to help", Toast.LENGTH_SHORT).show();
                        break;
                    case 8:
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
        toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("AI-AMS");
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
        NavigationView nav_view = (NavigationView) findViewById(R.id.nav_view);
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        View headerView = nav_view.getHeaderView(0);
        userEmail = headerView.findViewById(R.id.draw_user_email);
        userName = headerView.findViewById(R.id.draw_user_name);

        userName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName().toUpperCase());
        userEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
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
                drawer.closeDrawers();
                return true;
            }
        });

        // open drawer at start
//        drawer.openDrawer(GravityCompat.START);
    }
}

