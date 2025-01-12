package com.example.medioman;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Objects;

public class AdminLoginActivity21F21817 extends AppCompatActivity {

    private EditText adminEmailInput, adminPasswordInput;
    private Button adminLoginButton;
    private DatabaseReference adminDatabase;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login_medioman);

        adminDatabase = FirebaseDatabase.getInstance().getReference("Admins");
        adminEmailInput = findViewById(R.id.adminEmailInput);
        adminPasswordInput = findViewById(R.id.adminPasswordInput);
        adminLoginButton = findViewById(R.id.adminLoginButton);
        // Add password toggle visibility logic
        adminPasswordInput.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Drawable drawableEnd = adminPasswordInput.getCompoundDrawables()[2];
                if (drawableEnd != null && event.getRawX() >= (adminPasswordInput.getRight() - drawableEnd.getBounds().width())) {
                    togglePasswordVisibility();
                    return true;
                }
            }
            return false;
        });
        adminLoginButton.setOnClickListener(v -> loginAdmin());
    }
    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
        // Hide password
        adminPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        adminPasswordInput.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_lock, 0, R.drawable.ic_visibility_off_medioman, 0);
    } else {
        // Show password
        adminPasswordInput.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        adminPasswordInput.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_lock, 0, R.drawable.ic_visibility_on_medioman, 0);
    }
        adminPasswordInput.setSelection(adminPasswordInput.getText().length()); // Move cursor to end
        isPasswordVisible = !isPasswordVisible;
    }
    private void loginAdmin() {
        String email = adminEmailInput.getText().toString().trim();
        String password = adminPasswordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter admin email and password", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        adminDatabase.get().addOnCompleteListener(adminTask -> {
                            if (adminTask.isSuccessful() && adminTask.getResult().exists()) {
                                boolean isAuthenticated = false;

                                for (DataSnapshot adminSnapshot : adminTask.getResult().getChildren()) {
                                    String storedEmail = adminSnapshot.child("email").getValue(String.class);
                                    String storedPassword = adminSnapshot.child("password").getValue(String.class);

                                    if (storedEmail != null && storedEmail.equals(email) &&
                                            storedPassword != null && storedPassword.equals(password)) {
                                        isAuthenticated = true;
                                        break;
                                    }
                                }
                                if (isAuthenticated) {
                                    Intent intent = new Intent(AdminLoginActivity21F21817.this, AdminDashboardActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(AdminLoginActivity21F21817.this, "Invalid admin email or password", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(AdminLoginActivity21F21817.this, "Failed to access admin records", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(AdminLoginActivity21F21817.this, "Authentication failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
