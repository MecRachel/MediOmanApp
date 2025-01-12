package com.example.medioman;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewAmbulanceActivity21F21817 extends AppCompatActivity {

    private RecyclerView sosAlertRecyclerView;
    private SOSAlertAdapter21F21817 sosAlertAdapter;
    private List<SOSAlertActivity21F21817.SOSAlert> sosAlertList;
    private Button refreshButton;
    private TextView sosAlertStatusText;
    private String guestId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_ambulance_button_medioman);

        sosAlertRecyclerView = findViewById(R.id.sosAlertRecyclerView);
        refreshButton = findViewById(R.id.refreshButton);
        sosAlertStatusText = findViewById(R.id.sosAlertStatusText);
        sosAlertList = new ArrayList<>();

        guestId = "guest_" + Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        sosAlertAdapter = new SOSAlertAdapter21F21817(sosAlertList,
                this::onAlertSelected,
                this::showCancelConfirmationDialog,
                true);
        sosAlertRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        sosAlertRecyclerView.setAdapter(sosAlertAdapter);
        loadSOSAlerts();
        refreshButton.setOnClickListener(v -> loadSOSAlerts());
    }
    private void loadSOSAlerts() {
        sosAlertList.clear();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        final String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        DatabaseReference alertsRef = (userId != null) ?
                FirebaseDatabase.getInstance().getReference("Users").child(userId).child("useralerts") :
                FirebaseDatabase.getInstance().getReference("GuestSOSAlerts").child(guestId).child("guestalerts");
        alertsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                {
                    if (dataSnapshot.exists()) {
                        Log.d("DataSnapshot", "Data found for guest: " + dataSnapshot.toString());
                    } else {
                        Log.d("DataSnapshot", "No data found for guest.");
                    }
                }
                sosAlertList.clear();
                for (DataSnapshot alertSnapshot : dataSnapshot.getChildren()) {
                    SOSAlertActivity21F21817.SOSAlert sosAlert = alertSnapshot.getValue(SOSAlertActivity21F21817.SOSAlert.class);
                    if (sosAlert != null && ("Accepted".equals(sosAlert.status) || "Pending".equals(sosAlert.status) || "Cancelled".equals(sosAlert.status))) {

                        if (sosAlert.eta == null || sosAlert.eta.isEmpty()) {
                            sosAlert.eta = "ETA not available";
                        }
                        sosAlertList.add(sosAlert);
                    }
                }
                sosAlertAdapter.notifyDataSetChanged();
                sosAlertStatusText.setText(sosAlertList.isEmpty() ? "No alerts available." : "Displaying alerts.");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ViewAmbulanceActivity21F21817.this, "Error loading alerts: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showCancelConfirmationDialog(SOSAlertActivity21F21817.SOSAlert sosAlert) {
        new AlertDialog.Builder(ViewAmbulanceActivity21F21817.this)
                .setTitle("Cancel Alert")
                .setMessage("Are you sure you want to cancel this alert?")
                .setPositiveButton("Yes", (dialog, which) -> cancelAlert(sosAlert))
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }
    private void cancelAlert(SOSAlertActivity21F21817.SOSAlert sosAlert) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        DatabaseReference alertRef = (userId != null) ?
                FirebaseDatabase.getInstance().getReference("Users").child(userId).child("useralerts").child(sosAlert.id) :
                FirebaseDatabase.getInstance().getReference("GuestSOSAlerts").child(guestId).child("guestalerts").child(sosAlert.id);

        alertRef.child("status").setValue("Cancelled").addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Alert cancelled successfully.", Toast.LENGTH_SHORT).show();
                loadSOSAlerts();
            } else {
                Toast.makeText(this, "Failed to cancel alert.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void onAlertSelected(SOSAlertActivity21F21817.SOSAlert sosAlert) {
    }

}
