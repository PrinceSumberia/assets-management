package com.prince.assetManagement;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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

public class NormalUser extends AppCompatActivity {

    Button requestAsset, reportAsset, scanAsset;
    TextView logout;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar toolbar;
    private ActionBar actionBar;

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

    public static void changeMenuIconColor(Menu menu, @ColorInt int color) {
        for (int i = 0; i < menu.size(); i++) {
            Drawable drawable = menu.getItem(i).getIcon();
            if (drawable == null) continue;
            drawable.mutate();
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_user);
        initToolbar();
        initNavigationMenu();
        requestAsset = findViewById(R.id.request_asset);
//        reportAsset = findViewById(R.id.report_asset);
        scanAsset = findViewById(R.id.scan_asset_user);
//        logout = findViewById(R.id.logout_user);

//        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//            logout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    FirebaseAuth.getInstance().signOut();
//                    logout.setAlpha(0.0f);
//                    Toast.makeText(getApplicationContext(), "Successfully Logged You Out", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
//                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
//                            Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                    finish();
//                }
//            });
//        } else {
//            logout.setAlpha(0.0f);
//        }

        scanAsset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ScanActivity.class);
                startActivity(intent);
            }
        });


        requestAsset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RequestAsset.class);
                startActivity(intent);
                Toast.makeText(NormalUser.this, "Request Asset", Toast.LENGTH_SHORT).show();
            }
        });
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
            Toast.makeText(NormalUser.this, "Successfully Logged You Out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initNavigationMenu() {
        NavigationView nav_view = (NavigationView) findViewById(R.id.nav_view);
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
