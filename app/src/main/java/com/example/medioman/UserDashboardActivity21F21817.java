package com.example.medioman;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class UserDashboardActivity21F21817 extends AppCompatActivity {

    private ImageButton sendSOSButton;
    private ImageButton viewAmbulanceButton;
    private ImageButton viewProfileButton;
    private ImageButton feedbackButton;
    private ImageButton emergencyFirstAidButton;
    private DrawerLayout drawerLayout;
    private boolean isGuest;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard_medioman);

        firebaseAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView greetingTextView = findViewById(R.id.greetingTextView);

        sendSOSButton = findViewById(R.id.sendSOSButton);
        viewAmbulanceButton = findViewById(R.id.viewAmbulanceButton);
        viewProfileButton = findViewById(R.id.viewProfileButton);
        feedbackButton = findViewById(R.id.feedbackButton);
        emergencyFirstAidButton = findViewById(R.id.emergencyFirstAidButton);
        TextView userInfoTextView = findViewById(R.id.userInfoTextView);
        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ImageButton faqButton = findViewById(R.id.faqButton);

        faqButton.setOnClickListener(v -> {
            Toast.makeText(this, "Opening FAQ...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(UserDashboardActivity21F21817.this, FAQActivity21F21817.class));
        });

        isGuest = getIntent().getBooleanExtra("isGuest", false);

        if (!isGuest) {
            String userId = firebaseAuth.getCurrentUser().getUid();
            DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference("Users").child(userId);

            userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        String fullName = dataSnapshot.child("fullName").getValue(String.class);

                        userInfoTextView.setText(fullName != null ? fullName : "User");

                        String greetingMessage = getGreetingMessage(fullName != null ? fullName : "User");
                        greetingTextView.setText(greetingMessage);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(UserDashboardActivity21F21817.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            userInfoTextView.setText("Guest User");
            String greetingMessage = getGreetingMessage("Guest");
            greetingTextView.setText(greetingMessage);
        }

        if (isGuest) {
            setupGuestDashboard();
            disableProfileMenuItem(navigationView);
        } else {
            setupUserDashboard();
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            handleNavigationItemSelected(item);
            return true;
        });
    }
    private String getGreetingMessage(String userName) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (hour < 12) {
            return "Good Morning, " + userName;
        } else if (hour < 18) {
            return "Good Afternoon, " + userName;
        } else {
            return "Good Evening, " + userName;
        }
    }
    private void disableProfileMenuItem(NavigationView navigationView) {
        Menu menu = navigationView.getMenu();
        MenuItem profileItem = menu.findItem(R.id.nav_item2);
        profileItem.setEnabled(false);
        profileItem.setVisible(false);
    }

    private void handleNavigationItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.nav_item1) { // Home
            Toast.makeText(this, "Home clicked", Toast.LENGTH_SHORT).show();
        } else if (item.getItemId() == R.id.nav_item2) { // Profile
            if (isGuest) {
                Toast.makeText(this, "Profile option is disabled for guests.", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(this, ProfileActivity21F21817.class));
            }
        } else if (item.getItemId() == R.id.nav_item3) {
            startActivity(new Intent(this, EmergencyFirstAidActivity21F21817.class));
        } else if (item.getItemId() == R.id.nav_item4) {
            startActivity(new Intent(this, FeedbackActivity21F21817.class));
        } else if (item.getItemId() == R.id.nav_logout) {
            handleLogout();
        } else if (item.getItemId() == R.id.nav_about_us) {
            startActivity(new Intent(this, AboutUsActivity21F21817.class));
        } else if (item.getItemId() == R.id.nav_contact_us) {
                startActivity(new Intent(this, ContactUsActivity21F21817.class));
        } else if (item.getItemId() == R.id.nav_termsAndConditions) {
            startActivity(new Intent(this, TermsAndConditionsActivity.class));
        } else {
            Toast.makeText(this, "Unknown item clicked", Toast.LENGTH_SHORT).show();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
    }
    private void handleLogout() {
        firebaseAuth.signOut();
        Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
        Intent loginIntent = new Intent(this, LoginActivity21F21817.class);
        startActivity(loginIntent);
        finish();
    }
    private void setupGuestDashboard() {
        viewProfileButton.setOnClickListener(v -> {
            Toast.makeText(this, "Profile is available only for logged-in users.", Toast.LENGTH_SHORT).show();
        });
        viewProfileButton.setEnabled(false);
        viewProfileButton.setAlpha(0.5f);

        Toast.makeText(this, "Guest access: Limited features available", Toast.LENGTH_SHORT).show();

        setButtonClickListener(emergencyFirstAidButton, EmergencyFirstAidActivity21F21817.class, "Accessing Emergency First Aid Information...");

        setButtonClickListener(feedbackButton, TermsAndConditionsActivity.class, "Opening Terms and Conditions page...");

        setButtonClickListener(sendSOSButton, SOSAlertActivity21F21817.class, "Navigating to SOS Alert...");
        setButtonClickListener(viewAmbulanceButton, ViewAmbulanceActivity21F21817.class, "Fetching Ambulance Status...");
        setButtonClickListener(feedbackButton, FeedbackActivity21F21817.class, "Opening Feedback Form...");
    }
    private void setupUserDashboard() {
        setButtonClickListener(sendSOSButton, SOSAlertActivity21F21817.class, "Navigating to SOS Alert...");
        setButtonClickListener(viewAmbulanceButton, ViewAmbulanceActivity21F21817.class, "Fetching Ambulance Status...");
        setButtonClickListener(viewProfileButton, ProfileActivity21F21817.class, "Viewing Profile...");
        setButtonClickListener(emergencyFirstAidButton, EmergencyFirstAidActivity21F21817.class, "Accessing Emergency First Aid Information...");
        setButtonClickListener(feedbackButton, FeedbackActivity21F21817.class, "Opening Feedback Form...");

    }
    private void setButtonClickListener(ImageButton button, Class<?> cls, String message) {
        button.setOnClickListener(v -> {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(UserDashboardActivity21F21817.this, cls));
        });
    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
