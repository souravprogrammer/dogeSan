package com.sourav.dogesan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sourav.dogesan.utils.AnimePref;
import com.sourav.dogesan.utils.DogeViewModel;
import com.sourav.dogesan.utils.FirebaseContract;

import java.util.ArrayList;
import java.util.List;

public class SplashScreen extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private final FirebaseAuth.AuthStateListener authStateListener = firebaseAuth -> {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        final Intent intent;


        if (user != null) {
            DogeViewModel.USER_UID = user.getUid();
            intent = new Intent(getApplicationContext(), MainActivity.class);
        } else {
            intent = new Intent(getApplicationContext(), AuthActivity.class);
        }

        new Thread(() -> {
            try {
                Thread.sleep(200);
                SplashScreen.this.runOnUiThread(() -> {
                    startActivity(intent);
                    finish();
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();


    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();

        DogeViewModel.setMyAnimeList(new ArrayList<>());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onPause() {
        super.onPause();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}