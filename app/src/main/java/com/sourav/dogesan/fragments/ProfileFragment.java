package com.sourav.dogesan.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sourav.dogesan.R;
import com.sourav.dogesan.SplashScreen;
import com.sourav.dogesan.utils.DogeViewModel;
import com.sourav.dogesan.utils.FirebaseContract;
import com.sourav.dogesan.utils.UserData;

import java.util.Objects;


public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference profileReference;
    private TextView username ;
    private ProgressBar profileProgressBar;
    private ConstraintLayout profileView ;
    public ProfileFragment() {

        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        profileReference = mFirebaseDatabase.getReference(FirebaseContract.USER)
                .child(DogeViewModel.USER_UID).child(FirebaseContract.UserProfile);
        username = view.findViewById(R.id.username);
        profileView = view.findViewById(R.id.profile_view);
        profileProgressBar = view.findViewById(R.id.profile_progressbar);
        profileReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                UserData userData = snapshot.getValue(UserData.class);
                assert userData != null;
                username.setText(userData.getName());
                profileProgressBar.setVisibility(View.GONE);
                profileView.setVisibility(View.VISIBLE);
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
        }) ;

        Button button = view.findViewById(R.id.signout);
        button.setOnClickListener(v->{
           FirebaseAuth auth =   FirebaseAuth.getInstance();
           auth.signOut();
            Intent intent =new Intent(getContext(), SplashScreen.class);
            startActivity(intent);
            Objects.requireNonNull(getActivity()).finish();
        });
        return view;
    }
}