package com.example.medioman;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.UUID;

public class FeedbackActivity21F21817 extends AppCompatActivity {
    private EditText feedbackText;
    private Button submitButton;
    private RatingBar ratingBar;
    private DatabaseReference feedbackDatabase;
    private FirebaseAuth firebaseAuth;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private boolean isGuest = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_medioman);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            isGuest = true; // No user is logged in, so it's a guest
        } else {
            isGuest = false; // User is logged in
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        NavigationDrawerUtil.setupNavigationDrawer(this, drawerLayout, toolbar, navigationView, isGuest);
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        feedbackText = findViewById(R.id.feedbackText);
        submitButton = findViewById(R.id.submitButton);
        ratingBar = findViewById(R.id.ratingBar);

        feedbackDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        submitButton.setOnClickListener(v -> submitFeedback());
    }
    private void submitFeedback() {
        String feedback = feedbackText.getText().toString().trim();
        float rating = ratingBar.getRating();

        if (!feedback.isEmpty()) {
            if (firebaseAuth.getCurrentUser() != null) {
                String userId = firebaseAuth.getCurrentUser().getUid();
                DatabaseReference userFeedbackRef = feedbackDatabase.child("Users").child(userId).child("Feedback");
                String feedbackId = userFeedbackRef.push().getKey();
                userFeedbackRef.child(feedbackId).setValue(feedback + " | Rating: " + rating)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                feedbackText.setText("");
                                Toast.makeText(FeedbackActivity21F21817.this, "Feedback submitted successfully!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(FeedbackActivity21F21817.this, "Failed to submit feedback. Try again.", Toast.LENGTH_SHORT).show();
                            }
                        });

            } else {
                String guestId = UUID.randomUUID().toString();
                DatabaseReference guestFeedbackRef = feedbackDatabase.child("GuestFeedback").child(guestId);
                String feedbackId = guestFeedbackRef.push().getKey();
                guestFeedbackRef.child(feedbackId).setValue(feedback + " | Rating: " + rating)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                feedbackText.setText("");
                                Toast.makeText(FeedbackActivity21F21817.this, "Feedback submitted successfully!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(FeedbackActivity21F21817.this, "Failed to submit feedback. Try again.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        } else {
            Toast.makeText(FeedbackActivity21F21817.this, "Please enter your feedback", Toast.LENGTH_SHORT).show();
        }
    }
}
