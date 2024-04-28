package com.example.projectjava.UI.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectjava.R;
import com.example.projectjava.UI.BeginningActivity;
import com.example.projectjava.UI.bench.BenchResultsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        TextView emailText = findViewById(R.id.editTextTextEmailAddress);
        TextView passwordText = findViewById(R.id.editTextTextPassword);
        TextView passwordConfirmText = findViewById(R.id.editTextTextPasswordConfirm);

        findViewById(R.id.buttonConfirmRegister).setOnClickListener(v -> {

            String email = emailText.getText().toString();
            String password = passwordText.getText().toString();
            String passwordConfirm = passwordConfirmText.getText().toString();

            if (email.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Please fill all fields first.", Toast.LENGTH_LONG).show();
            } else if (!password.equals(passwordConfirm)) {
                Toast.makeText(RegisterActivity.this, "Password don't match.", Toast.LENGTH_LONG).show();
            } else {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("RegisterActivity", "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put("name", "John Doe");
                                userMap.put("email", email);
                                // Save user details
                                assert user != null;
                                db.collection("users").document(user.getUid()).set(userMap);

                                db.collection("users").document(user.getUid()).get().addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        String name = documentSnapshot.getString("name");
                                        System.out.println(name);
                                        // Update your UI
                                    }
                                });

                                startActivity(new Intent(this, BeginningActivity.class));
                            } else {
                                // If sign in fails, display a message to the user.
                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    Toast.makeText(getApplicationContext(), "The email address is already in use by another account.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Registration failed.", Toast.LENGTH_SHORT).show();
                                }
                                Log.w("RegisterActivity", "createUserWithEmail:failure", task.getException());
                            }
                        });
            }
        });
    }
}
