package com.example.projectjava.UI.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectjava.R;
import com.example.projectjava.UI.BeginningActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        TextView emailText = findViewById(R.id.editTextEmailLogin);
        TextView passwordText = findViewById(R.id.editTextPasswordLogin);

        findViewById(R.id.buttonConfirmLogin).setOnClickListener(v -> {

            String email = emailText.getText().toString();
            String password = passwordText.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please fill all fields first.", Toast.LENGTH_LONG).show();
            }

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
               if (task.isSuccessful()) {
                   // Sign in success, update UI with the signed-in user's information
                   Log.d("LoginActivity", "signInWithEmail:success");
                   FirebaseUser user = mAuth.getCurrentUser();
                   startActivity(new Intent(this, BeginningActivity.class));
               } else {
                   // If sign in fails, display a message to the user.
                   if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                       Toast.makeText(LoginActivity.this, "Invalid Credentials.",
                               Toast.LENGTH_SHORT).show();
                   } else {
                       Toast.makeText(LoginActivity.this, "Authentication failed.",
                               Toast.LENGTH_SHORT).show();
                   }
                   Log.w("LoginActivity", "signInWithEmail:failure", task.getException());
               }

            });

        });
    }
}
