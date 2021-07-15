package com.capstone_team_capstone_meal_master;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUpActivity extends AppCompatActivity {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    EditText etName, etEmail, etPassword, etConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        etName = findViewById(R.id.etName);
        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
    }


    public void signUp() {

        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Snackbar.make(findViewById(android.R.id.content), "Please fill all details", Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Snackbar.make(findViewById(android.R.id.content), "Password should be greater than 6 characters", Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Snackbar.make(findViewById(android.R.id.content), "Passwords should be same", Snackbar.LENGTH_SHORT).show();
            return;
        }
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                FirebaseUser user = task.getResult().getUser();
                UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build();
                user.updateProfile(userProfileChangeRequest);
                startActivity(new Intent(this, HomeActivity.class));

            } else {
                Log.w("MYMSG", task.getException());
            }
        });

    }


    public void saveUser(View view) {

        signUp();
    }
}