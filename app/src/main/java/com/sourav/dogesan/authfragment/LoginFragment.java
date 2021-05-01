package com.sourav.dogesan.authfragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.sourav.dogesan.MainActivity;
import com.sourav.dogesan.R;
import com.sourav.dogesan.utils.DogeViewModel;

import java.util.Objects;


public class LoginFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private EditText email, password;
    private TextView register;
    private Button login_button;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener listener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                DogeViewModel.USER_UID = user.getUid();
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
                Objects.requireNonNull(getActivity()).finish();
            } else {
                Toast.makeText(getContext(), "not", Toast.LENGTH_LONG).show();

            }
        }
    };

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onPause() {
        super.onPause();
        auth.removeAuthStateListener(listener);
    }

    @Override
    public void onResume() {
        super.onResume();
        auth.addAuthStateListener(listener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        auth = FirebaseAuth.getInstance();
        email = view.findViewById(R.id.emailfield_register);
        password = view.findViewById(R.id.passwordfield_register);
        register = view.findViewById(R.id.register);
        login_button = view.findViewById(R.id.register_button);
        login_button.setOnClickListener((V) -> {
            login();
        });
        register.setOnClickListener(v -> {
            assert getFragmentManager() != null;
            getFragmentManager().beginTransaction().replace(R.id.fragment_container_auth, new RegisterFragment()).commit();
            DogeViewModel.isregister = true;
        });
        return view;
    }

    private void login() {

        String email_input = email.getText().toString();
        String password_input = password.getText().toString();

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

        auth.signInWithEmailAndPassword(email_input, password_input).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    try {
                        throw task.getException();
                    } // if user enters wrong email.
                    catch (FirebaseAuthWeakPasswordException weakPassword) {
                        email.setError("Invalid Email");
                    }
                    // if user enters wrong password.
                    catch (FirebaseAuthInvalidCredentialsException malformedEmail) {

                        password.setError("Wrong password");
                    } catch (Exception e) {

                        email.setError("Invalid Email");
                        password.setError("Wrong password");

                    }
                }
            }
        });
    }


}