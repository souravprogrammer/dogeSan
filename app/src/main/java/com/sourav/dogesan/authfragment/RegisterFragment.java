package com.sourav.dogesan.authfragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sourav.dogesan.R;
import com.sourav.dogesan.utils.FirebaseContract;
import com.sourav.dogesan.utils.UserData;


public class RegisterFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private boolean email_enable = false;
    private boolean passworld_enable = false;
    private EditText email, password, name;
    private Button registerButtion;
    private UserData register_user;
    private FirebaseAuth auth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mFirebaseUser;
    private ProgressBar progressBar;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        // initializing fire base instance
        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        //  mFirebaseUser = firebaseDatabase.getReference(FirebaseContract.USER);
        mFirebaseUser = firebaseDatabase.getReference(FirebaseContract.USER);


        // initializing local variable
        progressBar = view.findViewById(R.id.progressBar_register);
        email = view.findViewById(R.id.emailfield_register);
        password = view.findViewById(R.id.passwordfield_register);
        name = view.findViewById(R.id.namefield_register);
        registerButtion = view.findViewById(R.id.register_button);
        email.addTextChangedListener(emailWatcher);
        password.addTextChangedListener(passwordWatcher);
        registerButtion.setOnClickListener(v -> {
            register();
            //call fire base here
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        auth.addAuthStateListener(listner);
    }

    @Override
    public void onPause() {
        super.onPause();
        auth.removeAuthStateListener(listner);
    }

    private void register() {
        String email_input = email.getText().toString();
        String password_input = password.getText().toString();
        String name_input = name.getText().toString();

        if (email_input.isEmpty()) {
            email.setError("Please provide Email");
            return;
        }
        if (password_input.isEmpty()) {
            password.setError("Please provide password");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email_input).matches()) {
            email.setError("invalid Email");
            password.setText("");
            return;
        }
        if (name_input.isEmpty()) {
            name.setError("Fill your name");
            return;
        }
        register_user = new UserData(email_input, password_input, name_input);
        progressBar.setVisibility(View.VISIBLE);
        auth.createUserWithEmailAndPassword(email_input, password_input).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (!task.isSuccessful()) {
                    try {
                        throw task.getException();
                    } catch (Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Email already exits", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
//        .addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(getContext(), "fail", Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    private TextWatcher passwordWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if (s.toString().length() > 1) {
                passworld_enable = true;
            } else {
                passworld_enable = false;
            }
            registerButtonState();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private TextWatcher emailWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if (s.toString().length() > 1) {
                email_enable = true;
            } else {
                email_enable = false;
            }
            registerButtonState();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void registerButtonState() {
        if (email_enable && passworld_enable) {
            registerButtion.setEnabled(true);
        } else {
            registerButtion.setEnabled(false);

        }
    }

    //Auth listener
    private FirebaseAuth.AuthStateListener listner = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // mFirebaseUser.child(user.getUid())  .push().setValue(register_user).addOnCompleteListener(new OnCompleteListener<Void>() {

                mFirebaseUser.child(user.getUid()).child(FirebaseContract.UserProfile).push()
                        .setValue(register_user)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getContext(), "you have been registered", Toast.LENGTH_SHORT).show();
                                    //  getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_auth
                                    //    , new LoginFragment()).commit();
                                } else {
                                    Toast.makeText(getContext(), "Unable to register", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            } else {
                // registration fail
                Toast.makeText(getContext(), "Registration fail", Toast.LENGTH_LONG).show();

            }

        }
    };
}