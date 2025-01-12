package com.example.medioman;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity21F21817 extends AppCompatActivity {
    private EditText emailInput;
    private Button resetButton;
    private FirebaseAuth mAuth;
    private TextView thankYouMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_medioman);

        emailInput = findViewById(R.id.emailInput);
        resetButton = findViewById(R.id.resetButton);
        thankYouMessage = findViewById(R.id.thankYouMessage);
        thankYouMessage.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        resetButton.setOnClickListener(view -> resetPassword());
    }
    private void resetPassword() {
        String email = emailInput.getText().toString().trim();

        if (email.isEmpty()) {
            emailInput.setError("Email is required");
            emailInput.requestFocus();
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        thankYouMessage.setVisibility(View.VISIBLE);
                        Toast.makeText(ForgotPasswordActivity21F21817.this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ForgotPasswordActivity21F21817.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
