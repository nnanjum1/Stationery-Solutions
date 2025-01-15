package com.example.stationerysolutions;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth auth; // Firebase Auth
    private DatabaseHelper dbHelper; // SQLite Database Helper
    private FirebaseFirestore firestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Auth and SQLite Database
        auth = FirebaseAuth.getInstance();
        dbHelper = new DatabaseHelper(this);

        EditText edSignUpUsername = findViewById(R.id.signup_username);
        EditText edSignUpEmail = findViewById(R.id.signup_email);
        EditText edPassWord = findViewById(R.id.signup_password);
        EditText edConfirmPassword = findViewById(R.id.signup_confirm_password);
        EditText edSignUpPhone = findViewById(R.id.signup_phone);
        TextView tvLogIn = findViewById(R.id.text_log_in);
        Button btn_SignUp = findViewById(R.id.s_btn_signup);

        // Regex patterns
        Pattern namePattern = Pattern.compile("[a-zA-Z._ ]+");
        Pattern emailPattern = Pattern.compile("^[a-zA-Z]+[a-zA-Z0-9._%+-]*@gmail\\.com$");
        Pattern phonePattern = Pattern.compile("^(\\+88)?01[2-9][0-9]{8}$");
        Pattern passwordPattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{6,}$");


        // Navigate to login
        tvLogIn.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
            startActivity(intent);
        });

        // Sign Up Button Click Logic
        btn_SignUp.setOnClickListener(v -> {
            String username = edSignUpUsername.getText().toString();
            String email = edSignUpEmail.getText().toString();
            String password = edPassWord.getText().toString();
            String confirmPassword = edConfirmPassword.getText().toString();
            String phone = edSignUpPhone.getText().toString();

            // Validation
            if (username.isEmpty()) {
                edSignUpUsername.setError("You must fill this field");
                edSignUpUsername.requestFocus();
                return;
            }

            if (!namePattern.matcher(username).matches()) {
                edSignUpUsername.setError("Username should contain only letters, dot, and space");
                edSignUpUsername.requestFocus();
                return;
            }

            if (email.isEmpty()) {
                edSignUpEmail.setError("You must fill this field");
                edSignUpEmail.requestFocus();
                return;
            }

            if (!emailPattern.matcher(email).matches()) {
                edSignUpEmail.setError("Only gmail is valid");
                edSignUpEmail.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                edPassWord.setError("You must fill this field");
                edPassWord.requestFocus();
                return;
            }

            if (!passwordPattern.matcher(password).matches()) {
                edPassWord.setError("Password should contain one lowercase, one upper case, one number,  and must be at least 6 characters");
                edPassWord.requestFocus();
                return;
            }

            if (confirmPassword.isEmpty()) {
                edConfirmPassword.setError("You must fill this field");
                edConfirmPassword.requestFocus();
                return;
            }

            if (!password.equals(confirmPassword)) {
                edConfirmPassword.setError("Password and confirm password do not match");
                edConfirmPassword.requestFocus();
                return;
            }

            if (phone.isEmpty()) {
                edSignUpPhone.setError("You must fill this field");
                edSignUpPhone.requestFocus();
                return;
            }

            if (!phonePattern.matcher(phone).matches()) {
                edSignUpPhone.setError("Only BD phone number is valid");
                edSignUpPhone.requestFocus();
                return;
            }

            // Create user in Firebase Authentication
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                // Send verification email
                                user.sendEmailVerification()
                                        .addOnCompleteListener(emailTask -> {
                                            if (emailTask.isSuccessful()) {
                                                Toast.makeText(SignUpActivity.this, "Verification email sent. Please check your inbox.", Toast.LENGTH_SHORT).show();
                                                storeUserInSQLite(username, email, password, phone);
                                            } else {
                                                Toast.makeText(SignUpActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            // Handle sign-up failure
                            Toast.makeText(SignUpActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        });
    }

    private void storeUserInSQLite(String username, String email, String password, String phone) {
        boolean isInserted = dbHelper.insertUser(username, email, password, phone);  // Insert user data into SQLite
        if (isInserted) {
            Toast.makeText(SignUpActivity.this, "User data stored in SQLite successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(SignUpActivity.this, "Failed to store user data in SQLite", Toast.LENGTH_SHORT).show();
        }
    }

    // Send email verification
//    private void sendEmailVerification(FirebaseUser user) {
//        user.sendEmailVerification()
//                .addOnCompleteListener(this, task -> {
//                    if (task.isSuccessful()) {
//                        Toast.makeText(SignUpActivity.this, "Verification email sent to " + user.getEmail(), Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
//                        startActivity(intent);
//                        finish();
//                    } else {
//                        Toast.makeText(SignUpActivity.this, "Failed to send verification email", Toast.LENGTH_LONG).show();
//                    }
//                });
//    }
}
