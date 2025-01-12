package com.example.medioman;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AdminDashboardActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "AdminDashboardActivity";
    private static final String CHANNEL_ID = "sos_alert_channel";

    private Button logOutButton;
    private RecyclerView recyclerView;
    private SOSAlertAdapter21F21817 adapter;
    private List<SOSAlertActivity21F21817.SOSAlert> sosAlerts;
    private TextView sosAlertDetails;
    private Button acceptAlertButton;
    private DatabaseReference sosDatabase;
    private TextView userLocationLink;
    private FirebaseAuth firebaseAuth;
    private Set<String> notifiedAlertIds = new HashSet<>();

    private Set<String> acknowledgedAlertIds = new HashSet<>();

    private GoogleMap mMap;

    private MediaPlayer mediaPlayer;

    private Button acknowledgeButton;

    private Button setEtaButton;


    private SOSAlertActivity21F21817.SOSAlert currentSOSAlert;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard_medioman);

        initializeUIElements();
        initializeFirebase();
        setupRecyclerView();
        setupBottomNavigation();
        createNotificationChannel();
        requestNotificationPermission();
        fetchSOSAlerts();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        mediaPlayer = MediaPlayer.create(this, R.raw.ringtonesos);
        mediaPlayer.setLooping(true); // Set the tone to play continuously

        acknowledgeButton.setOnClickListener(v -> {
            stopAlertTone(); // Stop the ringing
            acknowledgeButton.setVisibility(View.GONE);
            if (currentSOSAlert != null) {
                acknowledgedAlertIds.add(currentSOSAlert.id);
            }
            Toast.makeText(this, "Alert tone stopped", Toast.LENGTH_SHORT).show();
        });
    }
    private void initializeUIElements() {
        logOutButton = findViewById(R.id.logOutButton);
        recyclerView = findViewById(R.id.recyclerView);
        sosAlertDetails = findViewById(R.id.sosAlertDetails);
        acceptAlertButton = findViewById(R.id.acceptAlertButton);
        userLocationLink = findViewById(R.id.userLocationLink);
        acknowledgeButton = findViewById(R.id.acknowledgeButton);
        setEtaButton = findViewById(R.id.setEtaButton);
        logOutButton.setOnClickListener(v -> logOut());
    }

    private void initializeFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        sosDatabase = FirebaseDatabase.getInstance().getReference("SOSAlerts");
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    saveAdminToken(task.getResult());
                });
    }
    private void setupRecyclerView() {
        sosAlerts = new ArrayList<>();
        adapter = new SOSAlertAdapter21F21817(sosAlerts, this::onAlertSelected, this::onCancelAlert, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);
    }
    private void fetchSOSAlerts() {
        List<SOSAlertActivity21F21817.SOSAlert> userAlerts = new ArrayList<>();
        List<SOSAlertActivity21F21817.SOSAlert> guestAlerts = new ArrayList<>();

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
        DatabaseReference guestSOSRef = FirebaseDatabase.getInstance().getReference("GuestSOSAlerts");

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userAlerts.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot alertSnapshot : userSnapshot.child("useralerts").getChildren()) {
                        SOSAlertActivity21F21817.SOSAlert sosAlert = alertSnapshot.getValue(SOSAlertActivity21F21817.SOSAlert.class);
                        if (sosAlert != null && "user".equals(sosAlert.type)) {
                            userAlerts.add(sosAlert);
                            if ("user".equals(sosAlert.type) && "Pending".equals(sosAlert.status)) {
                                sendNotificationToAdmin(sosAlert);
                        }}
                    }
                }
                updateSOSAlerts(userAlerts, guestAlerts);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase Error", "Error fetching user SOS alerts: " + databaseError.getMessage());
            }
        });

        guestSOSRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                guestAlerts.clear();
                for (DataSnapshot guestSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot alertSnapshot : guestSnapshot.child("guestalerts").getChildren()) {
                        SOSAlertActivity21F21817.SOSAlert guestSOSAlert = alertSnapshot.getValue(SOSAlertActivity21F21817.SOSAlert.class);
                        if (guestSOSAlert != null && "guest".equals(guestSOSAlert.type)) {
                            guestAlerts.add(guestSOSAlert);
                            if ("Pending".equals(guestSOSAlert.status)) {
                                sendNotificationToAdmin(guestSOSAlert);
                                playAlertTone();
                            }
                        }
                    }
                }
                updateSOSAlerts(userAlerts, guestAlerts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase Error", "Error fetching guest SOS alerts: " + databaseError.getMessage());
            }
        });
    }
    private void updateSOSAlerts(List<SOSAlertActivity21F21817.SOSAlert> userAlerts, List<SOSAlertActivity21F21817.SOSAlert> guestAlerts) {
        sosAlerts.clear();
        sosAlerts.addAll(userAlerts);
        sosAlerts.addAll(guestAlerts);
        adapter.notifyDataSetChanged();
    }
    private void logOut() {
        firebaseAuth.signOut();
        startActivity(new Intent(AdminDashboardActivity.this, LoginActivity21F21817.class));
        finish();
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_home) {
            startActivity(new Intent(this, AdminDashboardActivity.class));
            return true;
        } else if (itemId == R.id.nav_reports) {
            startActivity(new Intent(this, ReportsActivity.class));
            return true;
        } else if (itemId == R.id.nav_adminfaq) {
            startActivity(new Intent(this, AdminFAQActivity21F21817.class));
            return true;
        }
        return false;
    }
    private void onAlertSelected(SOSAlertActivity21F21817.SOSAlert sosAlert) {
        sosAlertDetails.setText("SOS Alert from User: " + sosAlert.userId +
                "\nLocation: " + sosAlert.latitude + ", " + sosAlert.longitude);

        if (acknowledgedAlertIds.contains(sosAlert.id)) {
            acknowledgeButton.setVisibility(View.GONE);
        } else {
            acknowledgeButton.setVisibility(View.VISIBLE);
        }

        currentSOSAlert = sosAlert;

        if (mMap != null) {
            LatLng alertLocation = new LatLng(sosAlert.latitude, sosAlert.longitude);
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(alertLocation).title("SOS Alert"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(alertLocation, 15));
        }

        if ("Accepted".equals(sosAlert.status)) {
            acceptAlertButton.setVisibility(View.GONE);
            sosAlertDetails.append("\nStatus: Accepted");

            setEtaButton.setVisibility(View.VISIBLE);
            setEtaButton.setOnClickListener(v -> {
                showEtaInputDialog(sosAlert);
            });

            if (sosAlert.eta != null && !sosAlert.eta.isEmpty()) {
                sosAlertDetails.append("\nETA: " + sosAlert.eta);
            }
        } else if ("Cancelled".equals(sosAlert.status)) {
            acceptAlertButton.setVisibility(View.GONE);
            sosAlertDetails.append("\nStatus: Cancelled");
        } else {
            acceptAlertButton.setVisibility(View.VISIBLE);
            acceptAlertButton.setOnClickListener(v -> {
                stopAlertTone();
                acceptAlert(sosAlert);
            });
        }

        if ("user".equals(sosAlert.type)) {
            displayUserProfile(sosAlert);
        } else if ("guest".equals(sosAlert.type)) {
            displayGuestProfile(sosAlert);
        }
        acceptAlertButton.requestLayout();
        setupLocationLink(sosAlert);
    }
    private void showEtaInputDialog(SOSAlertActivity21F21817.SOSAlert sosAlert) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter ETA for SOS Alert");
        final EditText etaInput = new EditText(this);
        etaInput.setHint("Enter ETA (e.g., 15 minutes)");
        builder.setView(etaInput);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String eta = etaInput.getText().toString().trim();
            if (!eta.isEmpty()) {
                sosAlert.eta = eta;
                DatabaseReference sosAlertRef;
                if ("user".equals(sosAlert.type)) {

                    sosAlertRef = FirebaseDatabase.getInstance()
                            .getReference("Users")
                            .child(sosAlert.userId)
                            .child("useralerts")
                            .child(sosAlert.id);
                } else {
                    String guestId = sosAlert.userId;
                    sosAlertRef = FirebaseDatabase.getInstance()
                            .getReference("GuestSOSAlerts")
                            .child(guestId)
                            .child("guestalerts")
                            .child(sosAlert.id);
                }

                sosAlertRef.child("eta").setValue(eta)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "ETA Updated", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "ETA successfully updated for SOS alert: " + sosAlert.id);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Failed to update ETA", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Error updating ETA for SOS alert: " + sosAlert.id, e);
                        });
            } else {
                Toast.makeText(this, "ETA cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
    private void markAlertAsAcknowledged(SOSAlertActivity21F21817.SOSAlert sosAlert) {
        DatabaseReference alertRef;
        if ("guest".equals(sosAlert.type)) {
            alertRef = FirebaseDatabase.getInstance()
                    .getReference("GuestSOSAlerts")
                    .child(sosAlert.userId)
                    .child("guestalerts")
                    .child(sosAlert.id);
        } else {
            alertRef = FirebaseDatabase.getInstance()
                    .getReference("Users")
                    .child(sosAlert.userId)
                    .child("useralerts")
                    .child(sosAlert.id);
        }

        alertRef.child("status").setValue("Acknowledged")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Alert marked as acknowledged");
                    } else {
                        Log.e(TAG, "Failed to update alert status");
                    }
                });
    }
    private void setupLocationLink(SOSAlertActivity21F21817.SOSAlert sosAlert) {
        String location = sosAlert.latitude + "," + sosAlert.longitude;
        String mapsUrl = "https://www.google.com/maps/search/?api=1&query=" + location;

        userLocationLink.setText("View Location on Map");
        userLocationLink.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapsUrl));
            startActivity(intent);
        });
        userLocationLink.setVisibility(View.VISIBLE);
    }
    private void acceptAlert(SOSAlertActivity21F21817.SOSAlert sosAlert) {
        if ("Accepted".equals(sosAlert.status)) {
            Toast.makeText(this, "Alert already accepted!", Toast.LENGTH_SHORT).show();
            return;
        }

        sosAlert.status = "Accepted";
        adapter.notifyDataSetChanged();

        DatabaseReference alertRef;
        if ("guest".equals(sosAlert.type)) {
            alertRef = FirebaseDatabase.getInstance()
                    .getReference("GuestSOSAlerts")
                    .child(sosAlert.userId)
                    .child("guestalerts")
                    .child(sosAlert.id);
        } else {
            alertRef = FirebaseDatabase.getInstance()
                    .getReference("Users")
                    .child(sosAlert.userId)
                    .child("useralerts")
                    .child(sosAlert.id);
        }

        alertRef.child("status").setValue("Accepted").addOnSuccessListener(aVoid -> {
            Log.d("Alert Status", "SOS Alert accepted.");
        }).addOnFailureListener(e -> {
            Log.e("Firebase Error", "Failed to update alert status: " + e.getMessage());
        });
    }


    // Method to handle alert cancellation
    private void onCancelAlert(SOSAlertActivity21F21817.SOSAlert sosAlert) {
        if ("Accepted".equals(sosAlert.status)) {
            sosAlert.status = "Cancelled";
            adapter.notifyDataSetChanged();

            DatabaseReference alertRef;
            if ("guest".equals(sosAlert.type)) {
                alertRef = FirebaseDatabase.getInstance()
                        .getReference("GuestSOSAlerts")
                        .child(sosAlert.userId)
                        .child("guestalerts")
                        .child(sosAlert.id);
            } else {
                alertRef = FirebaseDatabase.getInstance()
                        .getReference("Users")
                        .child(sosAlert.userId)
                        .child("useralerts")
                        .child(sosAlert.id);
            }

            alertRef.child("status").setValue("Cancelled").addOnSuccessListener(aVoid -> {
                Log.d("Alert Status", "SOS Alert cancelled.");
            }).addOnFailureListener(e -> {
                Log.e("Firebase Error", "Failed to update alert status: " + e.getMessage());
            });
        } else {
            Toast.makeText(this, "Alert cannot be cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendNotificationToAdmin(SOSAlertActivity21F21817.SOSAlert sosAlert) {
        if (notifiedAlertIds.contains(sosAlert.id)) {
            return;
        }
        notifiedAlertIds.add(sosAlert.id);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent intent = new Intent(this, AdminDashboardActivity.class);
        intent.putExtra("ALERT_ID", sosAlert.id);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_sos)
                .setContentTitle("New SOS Alert from " + (sosAlert.fullName != null ? sosAlert.fullName : "Guest"))
                .setContentText("Location: " + sosAlert.latitude + ", " + sosAlert.longitude +
                        (sosAlert.phoneNumber != null ? "\nPhone: " + sosAlert.phoneNumber : ""))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(alarmSound)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        try {
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
            playAlertTone();
        } catch (SecurityException e) {
            Log.e(TAG, "Failed to show notification: Permission not granted", e);
        }
    }
    private void playAlertTone() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start(); // Play the alert tone
        }
    }
    private void stopAlertTone() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(this, R.raw.ringtonesos); // Reinitialize
        }
    }
    private void displayUserProfile(SOSAlertActivity21F21817.SOSAlert sosAlert) {
        String userProfile = "User Profile:\n" +
                "Name: " + sosAlert.fullName + "\n" +
                "Phone: " + sosAlert.phoneNumber + "\n" +
                "Emergency Contact: " + sosAlert.emergencyContact + "\n" +
                "Allergies: " + sosAlert.allergies + "\n" +
                "Blood Type: " + sosAlert.bloodType + "\n" +
                "Medical Conditions: " + sosAlert.medicalConditions + "\n" +
                "Date of Birth: " + sosAlert.dateOfBirth + "\n" +
                "Sex: " + sosAlert.sex;

        sosAlertDetails.append("\n" + userProfile);
    }

    private void displayGuestProfile(SOSAlertActivity21F21817.SOSAlert sosAlert) {
        String guestProfile = "Guest Profile:\n" +
                "Guest ID: " + sosAlert.userId;

        sosAlertDetails.append("\n" + guestProfile);
    }
    private void saveAdminToken(String token) {
        DatabaseReference adminDatabase = FirebaseDatabase.getInstance().getReference("AdminTokens");
        adminDatabase.child("admin").setValue(token)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Admin token saved successfully"))
                .addOnFailureListener(e -> Log.w(TAG, "Failed to save admin token", e));
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "SOS Alerts";
            String description = "Channel for SOS Alert notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    if (mMap != null) {
                        mMap.setMyLocationEnabled(true);
                    }
                } catch (SecurityException e) {
                    Log.e(TAG, "Failed to enable location layer: ", e);
                }
            } else {
                Toast.makeText(this, "Location permission denied. Cannot display user location.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void showNotificationWithRingtone(SOSAlertActivity21F21817.SOSAlert sosAlert) {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_sos)
                .setContentTitle("New SOS Alert from " + sosAlert.fullName)
                .setContentText("Location: " + sosAlert.latitude + ", " + sosAlert.longitude + "\nPhone: " + sosAlert.phoneNumber)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(alarmSound)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        try {
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        } catch (SecurityException e) {
            Log.e(TAG, "Failed to show notification: Permission not granted", e);
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng defaultLocation = new LatLng(23.5880, 58.3829);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10));

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try {
                mMap.setMyLocationEnabled(true);
            } catch (SecurityException e) {
                Log.e(TAG, "Failed to enable location layer: ", e);
            }
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        }
    }
    private String getGuestId() {
        return "guest_" + Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}



