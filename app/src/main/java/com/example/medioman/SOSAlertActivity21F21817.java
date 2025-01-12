package com.example.medioman;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SOSAlertActivity21F21817 extends AppCompatActivity {
    private Button sosButton;
    private ImageButton backButton;
    private DatabaseReference sosDatabase;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private CountDownTimer sosCountDownTimer;
    private boolean isCountingDown = false;
    private static final String YOUR_SERVER_KEY = "AIzaSyCi9FsHtAdWElVdeGE-LGtXjBobXP08eNE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sosalert_medioman);

        sosButton = findViewById(R.id.sosButton);
        backButton = findViewById(R.id.backButton);
        sosDatabase = FirebaseDatabase.getInstance().getReference("SOSAlerts");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            sosButton.setEnabled(true);
            Toast.makeText(this, "You can send SOS alerts as a guest.", Toast.LENGTH_SHORT).show();
        }
        requestLocationPermission();
        checkGPSAvailability();
        registerReceiver(gpsStateReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
        backButton.setOnClickListener(v -> {
            onBackPressed();
        });
        sosButton.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startSosCountdown();
                    return true;
                case MotionEvent.ACTION_UP:
                    cancelSosCountdown();
                    return true;
            }
            return false;
        });
        setupLogoutButton();
    }
    private void setupLogoutButton() {
        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setEnabled(true);
        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(SOSAlertActivity21F21817.this, "Successfully logged out", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(SOSAlertActivity21F21817.this, LoginActivity21F21817.class));
        });
    }
    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            requestNewLocation();
        }
    }
    private void checkGPSAvailability() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "GPS is not enabled. Please enable GPS.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);

        }}
    private BroadcastReceiver gpsStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (LocationManager.PROVIDERS_CHANGED_ACTION.equals(action)) {
                // Check if GPS is enabled
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    // GPS is enabled
                    Toast.makeText(SOSAlertActivity21F21817.this, "GPS is now enabled. You can send SOS alerts.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
    private void startSosCountdown() {
        if (isCountingDown) return;
        isCountingDown = true;
        sosCountDownTimer = new CountDownTimer(5000, 1000) {
            public void onTick(long millisUntilFinished) {
                long secondsLeft = millisUntilFinished / 1000;
                Toast.makeText(SOSAlertActivity21F21817.this, "Hold to send SOS Alert... " + secondsLeft + " seconds left", Toast.LENGTH_SHORT).show();
            }
            public void onFinish() {
                sendSOSAlert();
                isCountingDown = false;
            }
        }.start();
    }
    private void cancelSosCountdown() {
        if (sosCountDownTimer != null) {
            sosCountDownTimer.cancel();
            if (isCountingDown) {
                isCountingDown = false;
                Toast.makeText(SOSAlertActivity21F21817.this, "SOS Alert Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void sendSOSAlert() {
        if (currentLocation != null) {
            double latitude = currentLocation.getLatitude();
            double longitude = currentLocation.getLongitude();
            Log.d("SOS Location", "Latitude: " + latitude + ", Longitude: " + longitude);
            processSOSAlert(currentLocation);
            Toast.makeText(this, "SOS Alert Sent. Location: " + latitude + ", " + longitude, Toast.LENGTH_SHORT).show();
        } else {
            Log.d("SOS Alert", "Current location is null, requesting new location...");
            requestNewLocation();
            Toast.makeText(this, "Location not available. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
    private void requestNewLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(5000);

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            Log.d("Location Request", "Started requesting location updates.");
        } else {
            requestLocationPermission();
        }
    }
    private Location currentLocation;
    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                Log.d("Location Update", "LocationResult is null.");
                return;
            }
            currentLocation = locationResult.getLastLocation();
            currentLocation = locationResult.getLastLocation();
            Log.d("Updated Location", "Latitude: " + currentLocation.getLatitude() + ", Longitude: " + currentLocation.getLongitude());
        }
    };
    private void processSOSAlert(Location location) {
        String id = SOSAlert.generateUniqueId();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            String userId = firebaseAuth.getCurrentUser().getUid();
            String type = "user";
            DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference("Users").child(userId);

            userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String fullName = dataSnapshot.child("fullName").getValue(String.class);
                        String phoneNumber = dataSnapshot.child("phoneNumber").getValue(String.class);
                        String emergencyContact = dataSnapshot.child("emergencyContact").getValue(String.class);
                        String allergies = dataSnapshot.child("allergies").getValue(String.class);
                        String bloodType = dataSnapshot.child("bloodType").getValue(String.class);
                        String medicalConditions = dataSnapshot.child("medicalConditions").getValue(String.class);
                        String dateOfBirth = dataSnapshot.child("dateOfBirth").getValue(String.class);
                        String sex = dataSnapshot.child("sex").getValue(String.class);
                        SOSAlert sosAlert = new SOSAlert(id, userId, type, location.getLatitude(), location.getLongitude(),
                                System.currentTimeMillis(), fullName, phoneNumber, emergencyContact, allergies,
                                bloodType, medicalConditions,dateOfBirth, sex, "Pending", "Pending");

                        saveSosAlert(sosAlert, "Users", userId);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("Firebase Error", "Failed to fetch user profile: " + databaseError.getMessage());
                }
            });
        } else {
            String guestId = getGuestId();
            String type = "guest";
            SOSAlert sosAlert = new SOSAlert(id, guestId, type, location.getLatitude(), location.getLongitude(),
                    System.currentTimeMillis(), "Guest User", "N/A", "N/A", "N/A", "N/A", "N/A", "Unknown", "Unknown", "Pending", "Pending" );

            saveSosAlert(sosAlert, "GuestSOSAlerts", guestId);
        }
    }
    private String getGuestId() {
        return "guest_" + Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }
    private void saveSosAlert(SOSAlert sosAlert, String parentNode, String id) {
        Toast.makeText(SOSAlertActivity21F21817.this, "Sending SOS Alert...", Toast.LENGTH_SHORT).show();
        DatabaseReference sosAlertRef;

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            sosAlert.userId = userId;
            sosAlertRef = FirebaseDatabase.getInstance()
                    .getReference(parentNode)
                    .child(userId)
                    .child("useralerts")
                    .push();
        } else {
            String guestId = id;
            sosAlert.userId = guestId;
            sosAlertRef = FirebaseDatabase.getInstance()
                    .getReference(parentNode)
                    .child(guestId)
                    .child("guestalerts")
                    .push();
        }
        sosAlert.id = sosAlertRef.getKey();
        sosAlertRef.setValue(sosAlert).addOnSuccessListener(aVoid -> {
            Toast.makeText(SOSAlertActivity21F21817.this, "SOS Alert Sent", Toast.LENGTH_SHORT).show();
            sosButton.setEnabled(true);
        }).addOnFailureListener(e -> {
            Log.e("Firebase Error", "Failed to send SOS: " + e.getMessage());
            Toast.makeText(SOSAlertActivity21F21817.this, "Failed to send SOS: " + e.getMessage(), Toast.LENGTH_LONG).show();
            sosButton.setEnabled(true);
        });
    }
    private void sendNotificationToAdmin(SOSAlert sosAlert) {
        DatabaseReference adminTokenDatabase = FirebaseDatabase.getInstance().getReference("AdminTokens");
        adminTokenDatabase.child("admin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String adminToken = snapshot.getValue(String.class);
                    if (adminToken != null) {
                        String message = "SOS Alert from " + sosAlert.fullName + "!\n" +
                                "Location: " + sosAlert.latitude + ", " + sosAlert.longitude;
                        sendFCMNotification(adminToken, message);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase Error", "Failed to load admin token: " + error.getMessage());
            }
        });
    }
    private void sendFCMNotification(String token, String message) {
        new Thread(() -> {
            try {
                URL url = new URL("https://fcm.googleapis.com/fcm/send");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", "key=" + YOUR_SERVER_KEY);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject json = new JSONObject();
                json.put("to", token);
                json.put("notification", new JSONObject().put("body", message).put("title", "SOS Alert"));

                OutputStream os = conn.getOutputStream();
                os.write(json.toString().getBytes());
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.i("FCM", "Notification sent successfully");
                } else {
                    Log.e("FCM", "Failed to send notification: " + responseCode);
                }
            } catch (JSONException e) {
                Log.e("FCM JSON", "JSON Exception: " + e.getMessage());
            } catch (Exception e) {
                Log.e("FCM Error", "Error sending notification: " + e.getMessage());
            }
        }).start();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sosCountDownTimer != null) {
            sosCountDownTimer.cancel();
        }
        unregisterReceiver(gpsStateReceiver);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location permission granted", Toast.LENGTH_SHORT).show();
                requestNewLocation();
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Toast.makeText(this, "Location permission is required for SOS alerts. Please enable it in settings.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Location permission is required for SOS alerts", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    static class SOSAlert {
        public String id;
        public String userId;
        public String type;
        public double latitude;
        public double longitude;
        public long timestamp;
        public String fullName;
        public String phoneNumber;
        public String emergencyContact;
        public String allergies;
        public String bloodType;
        public String medicalConditions;
        public String dateOfBirth;
        public String sex;
        public String status;
        public String eta;
        public SOSAlert() {}
        public SOSAlert(String id, String userId, String type, double latitude, double longitude, long timestamp,
                        String fullName, String phoneNumber, String emergencyContact,
                        String allergies, String bloodType, String medicalConditions, String dateOfBirth, String sex,
                        String status, String eta) {
            this.id = id;
            this.userId = userId;
            this.type = type;
            this.latitude = latitude;
            this.longitude = longitude;
            this.timestamp = timestamp;
            this.fullName = fullName;
            this.phoneNumber = phoneNumber;
            this.emergencyContact = emergencyContact;
            this.allergies = allergies;
            this.bloodType = bloodType;
            this.medicalConditions = medicalConditions;
            this.dateOfBirth = dateOfBirth;
            this.sex = sex;
            this.status = status;
            this.eta = eta;
        }
    public static String generateUniqueId() {
            return "SOS_" + System.currentTimeMillis();
        }
        public String getFormattedTimestamp() {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm - dd MMM yyyy", Locale.getDefault());
            return sdf.format(new Date(timestamp));
        }
    }
}