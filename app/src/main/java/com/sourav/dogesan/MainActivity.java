package com.sourav.dogesan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sourav.dogesan.fragments.HomeFragment;
import com.sourav.dogesan.fragments.SearchFragment;
import com.sourav.dogesan.utils.DogeViewModel;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment;
    SearchFragment searchFragment;

    @Override
    protected void onResume() {
        super.onResume();
//        if (DogeViewModel.getSimpleExoPlayer()!=null){
//            DogeViewModel.setSimpleExoPlayer(null);
//        }
        if (DogeViewModel.getAnimeCard() != null) {
            DogeViewModel.setAnimeCard(null);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        searchFragment = new SearchFragment();
        homeFragment = new HomeFragment();
        bottomNavigationView.setOnNavigationItemSelectedListener(NavigationListner);
       // getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,homeFragment).commit();
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();
    }



    private final BottomNavigationView.OnNavigationItemSelectedListener NavigationListner
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.home_button:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
                    break;
                case R.id.search_navigation_button:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, searchFragment).commit();
                    break;
            }
            return true;
        }
    };


}