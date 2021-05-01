package com.sourav.dogesan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.sourav.dogesan.authfragment.LoginFragment;
import com.sourav.dogesan.authfragment.RegisterFragment;
import com.sourav.dogesan.utils.DogeViewModel;

public class AuthActivity extends AppCompatActivity {


    LoginFragment loginFragment ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        getSupportActionBar().hide();
        loginFragment = new LoginFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_auth,loginFragment).commit();

    }


    @Override
    public void onBackPressed() {

        if(DogeViewModel.isregister){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_auth,loginFragment).commit();
            DogeViewModel.isregister =false ;
            // only for debug purpose
        }else {
            super.onBackPressed();
        }


    }

}