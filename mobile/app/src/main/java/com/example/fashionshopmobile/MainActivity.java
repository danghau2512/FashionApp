package com.example.fashionshopmobile;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.fashionshopmobile.activity.OrderHistoryActivity;
import com.example.fashionshopmobile.activity.StoreMapActivity;
import com.example.fashionshopmobile.fragment.HomeFragment;
import com.example.fashionshopmobile.fragment.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigation = findViewById(R.id.bottomNavigation);

        setupBottomNavigation();

        if (savedInstanceState == null) {
            bottomNavigation.setSelectedItemId(R.id.nav_home);
        }
    }

    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                openFragment(new HomeFragment());
                return true;
            }

            if (itemId == R.id.nav_profile) {
                openFragment(new ProfileFragment());
                return true;
            }

            if (itemId == R.id.nav_store) {
                Intent intent = new Intent(MainActivity.this, StoreMapActivity.class);
                startActivity(intent);

                return false;
            }

            if (itemId == R.id.nav_orders) {
                Intent intent = new Intent(MainActivity.this, OrderHistoryActivity.class);
                startActivity(intent);
                return false;
            }

            return false;
        });
    }

    private void openFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}