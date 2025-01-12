package com.example.medioman;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity21F21817 extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button loginButton, registerButton, forgotPasswordButton, guestLoginButton, adminLoginButton;
    private ImageButton togglePasswordVisibility;
    private FirebaseAuth firebaseAuth;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_medioman);

        firebaseAuth = FirebaseAuth.getInstance();

        emailInput = findViewById(R.id.emailUsernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        forgotPasswordButton = findViewById(R.id.forgotPasswordButton);
        guestLoginButton = findViewById(R.id.guestLoginButton);
        adminLoginButton = findViewById(R.id.adminLoginButton);
        togglePasswordVisibility = findViewById(R.id.togglePasswordVisibility);

        loginButton.setOnClickListener(v -> loginUser());
        registerButton.setOnClickListener(v -> startActivity(new Intent(LoginActivity21F21817.this, RegisterActivity21F21817.class)));
        forgotPasswordButton.setOnClickListener(v -> startActivity(new Intent(LoginActivity21F21817.this, ForgotPasswordActivity21F21817.class)));

        guestLoginButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(LoginActivity21F21817.this, UserDashboardActivity21F21817.class);
            intent.putExtra("userName", "Guest User");
            intent.putExtra("isGuest", true);

            showTermsAndConditionsDialog(intent);
        });
        adminLoginButton.setOnClickListener(v -> {
            // Create an intent for Admin Login
            Intent intent = new Intent(LoginActivity21F21817.this, AdminLoginActivity21F21817.class);
            startActivity(intent);
        });

        togglePasswordVisibility.setOnClickListener(v -> {
            if (isPasswordVisible) {
                passwordInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                togglePasswordVisibility.setImageResource(R.drawable.ic_visibility_off_medioman);
            } else {
                passwordInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                togglePasswordVisibility.setImageResource(R.drawable.ic_visibility_on_medioman);
            }
            passwordInput.setSelection(passwordInput.getText().length());
            isPasswordVisible = !isPasswordVisible;
        });
    }
    private void loginUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches() && !password.isEmpty()) {
            signInWithEmail(email, password);
        } else {
            Toast.makeText(this, "Please enter a valid email and password", Toast.LENGTH_SHORT).show();
        }
    }
    private void signInWithEmail(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userName = email;
                        Intent intent = new Intent(LoginActivity21F21817.this, UserDashboardActivity21F21817.class);
                        intent.putExtra("userName", userName);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity21F21817.this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
    private void showTermsAndConditionsDialog(Intent intent) {
        // Create a TextView programmatically
        TextView termsLink = new TextView(this);
        termsLink.setText("Click here to view the full Terms and Conditions.");
        termsLink.setTextColor(getResources().getColor(android.R.color.holo_blue_light));
        termsLink.setClickable(true);
        termsLink.setMovementMethod(LinkMovementMethod.getInstance());

        termsLink.setOnClickListener(v -> {
            Intent termsIntent = new Intent(LoginActivity21F21817.this, TermsAndConditionsActivity.class);
            termsIntent.putExtra("fromRegisterOrGuest", true);
            startActivity(termsIntent);
        });

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);
        TextView messageText = new TextView(this);
        messageText.setText("Please accept the Terms and Conditions to continue.");
        layout.addView(messageText);
        layout.addView(termsLink);

        new AlertDialog.Builder(this)
                .setTitle("Terms and Conditions")
                .setView(layout)
                .setPositiveButton("Accept", (dialog, which) -> {
                    getSharedPreferences("app_preferences", MODE_PRIVATE)
                            .edit()
                            .putBoolean("terms_accepted", true)
                            .apply();
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Decline", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setCancelable(false)
                .show();
    }
}
