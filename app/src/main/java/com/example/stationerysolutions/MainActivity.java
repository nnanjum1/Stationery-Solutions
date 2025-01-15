package com.example.stationerysolutions;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private SharedPreferences sharedPreferences;
    private DatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize FirebaseAuth and SharedPreferences
        auth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("StationerySolutionsPrefs", MODE_PRIVATE);

        // Check session
        checkSession();

        EditText edEmail = findViewById(R.id.login_email);
        EditText edPassword = findViewById(R.id.login_password);
        Button btnLogin = findViewById(R.id.btn_login);
        TextView tvSignup = findViewById(R.id.text_sign_up);

        // Navigate to SignUpActivity
        tvSignup.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        // Handle Login button click
        btnLogin.setOnClickListener(v -> {
            String email = edEmail.getText().toString();
            String password = edPassword.getText().toString();

            if (email.isEmpty()) {
                edEmail.setError("Email should not be empty");
                edEmail.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                edPassword.setError("Password should not be empty");
                edPassword.requestFocus();
                return;
            }

            // Check if it's admin login
            if (email.equals("admin1@gmail.com") && password.equals("admin")) {
                // Admin login
                saveSession("admin");
                Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                startActivity(intent);
                finish();
            } else {
                // Firebase Authentication: Login with email and password
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = auth.getCurrentUser();
                                if (user != null && user.isEmailVerified()) {
                                    // User login with verified email
                                    saveSession("user");
                                    Intent intent = new Intent(MainActivity.this, UserActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(MainActivity.this, "Please verify your email first.", Toast.LENGTH_SHORT).show();
                                    auth.signOut();
                                }
                            } else {
                                // Authentication failed
                                Toast.makeText(MainActivity.this, "Invalid email or password!", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void checkSession() {
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        String userType = sharedPreferences.getString("userType", "");

        if (isLoggedIn) {
            if ("admin".equals(userType)) {
                navigateToActivity(AdminActivity.class);
            } else if ("user".equals(userType)) {
                navigateToActivity(UserActivity.class);
            }
        }
    }

    private void saveSession(String userType) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putString("userType", userType);
        editor.apply();
    }

    private void navigateToActivity(Class<?> targetActivity) {
        Intent intent = new Intent(MainActivity.this, targetActivity);
        startActivity(intent);
        finish();
    }
}
