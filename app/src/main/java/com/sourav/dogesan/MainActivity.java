package com.sourav.dogesan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sourav.dogesan.fragments.HomeFragment;
import com.sourav.dogesan.fragments.MyAnimeListFragment;
import com.sourav.dogesan.fragments.SearchFragment;
import com.sourav.dogesan.utils.DogeViewModel;
import com.sourav.dogesan.utils.FirebaseContract;
import com.sourav.dogesan.utils.MyAnimeList;
import com.sourav.dogesan.utils.UserData;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment;
    SearchFragment searchFragment;
    MyAnimeListFragment listFragment;
    FirebaseDatabase mfirebaseDatabase;
    DatabaseReference mUserReferenceuser;
    DatabaseReference mAnimeListReference;

    ChildEventListener childEventListenerUser = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            MyAnimeList item = snapshot.getValue(MyAnimeList.class);
            if (item != null) {
                item.setUID(snapshot.getKey());
                DogeViewModel.addMyAnimelist(item);
              //  Toast.makeText(getApplicationContext(), "" + user.getEmail(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot snapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

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

        // initialing the firebase reference
        mfirebaseDatabase = FirebaseDatabase.getInstance();
        mUserReferenceuser = mfirebaseDatabase.getReference(FirebaseContract.USER).child(DogeViewModel.USER_UID);
        mAnimeListReference = mUserReferenceuser .child(FirebaseContract.MY_LIST);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        searchFragment = new SearchFragment();
        homeFragment = new HomeFragment();
        listFragment = new MyAnimeListFragment();

        bottomNavigationView.setOnNavigationItemSelectedListener(NavigationListner);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
        ActionBar actionBar = getSupportActionBar();

        mAnimeListReference.addChildEventListener(childEventListenerUser);
        assert actionBar != null;
        actionBar.hide();
        // Toast.makeText(this,""+.)
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
                case R.id.mylist_navigation_button :
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,listFragment).commit();
            }
            return true;
        }
    };


}