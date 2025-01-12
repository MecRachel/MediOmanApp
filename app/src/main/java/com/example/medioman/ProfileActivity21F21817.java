package com.example.medioman;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class ProfileActivity21F21817 extends AppCompatActivity {
    private EditText nameInput, phoneInput, emergencyContactInput, allergiesInput, bloodTypeInput, medicalConditionsInput;
    private EditText dateOfBirthInput;
    private RadioGroup sexRadioGroup;
    private Button updateProfileButton;
    private DatabaseReference userDatabase;
    private FirebaseUser user;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private boolean isGuest = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_medioman);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            isGuest = true;
        } else {
            isGuest = false;
        }
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        NavigationDrawerUtil.setupNavigationDrawer(this, drawerLayout, toolbar, navigationView, isGuest);

        nameInput = findViewById(R.id.nameInput);
        phoneInput = findViewById(R.id.phoneInput);
        emergencyContactInput = findViewById(R.id.emergencyContactInput);
        allergiesInput = findViewById(R.id.allergiesInput);
        bloodTypeInput = findViewById(R.id.bloodTypeInput);
        medicalConditionsInput = findViewById(R.id.medicalConditionsInput);
        dateOfBirthInput = findViewById(R.id.dateOfBirthInput);
        sexRadioGroup = findViewById(R.id.sexRadioGroup);
        updateProfileButton = findViewById(R.id.updateProfileButton);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userDatabase = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        }
        loadUserProfile();
        updateProfileButton.setOnClickListener(view -> updateProfile());
        dateOfBirthInput.setOnClickListener(v -> showDatePickerDialog());
    }
    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            String dateOfBirth = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
            dateOfBirthInput.setText(dateOfBirth);
        }, year, month, day);

        datePickerDialog.show();
    }
    private void loadUserProfile() {
        if (userDatabase == null) return;

        userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("fullName").getValue(String.class);
                    String phone = snapshot.child("phoneNumber").getValue(String.class);
                    String emergencyContact = snapshot.child("emergencyContact").getValue(String.class);
                    String allergies = snapshot.child("allergies").getValue(String.class);
                    String bloodType = snapshot.child("bloodType").getValue(String.class);
                    String medicalConditions = snapshot.child("medicalConditions").getValue(String.class);
                    String dateOfBirth = snapshot.child("dateOfBirth").getValue(String.class);
                    String sex = snapshot.child("sex").getValue(String.class);

                    if (name != null) nameInput.setText(name);
                    if (phone != null) phoneInput.setText(phone);
                    if (emergencyContact != null) emergencyContactInput.setText(emergencyContact);
                    if (allergies != null) allergiesInput.setText(allergies);
                    if (bloodType != null) bloodTypeInput.setText(bloodType);
                    if (medicalConditions != null) medicalConditionsInput.setText(medicalConditions);
                    if (dateOfBirth != null) dateOfBirthInput.setText(dateOfBirth);
                    if (sex != null && sex.equalsIgnoreCase("male")) {
                        sexRadioGroup.check(R.id.radioMale);
                    } else if (sex != null && sex.equalsIgnoreCase("female")) {
                        sexRadioGroup.check(R.id.radioFemale);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity21F21817.this, "Failed to load profile data", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateProfile() {
        // Get the values from input fields
        String name = nameInput.getText().toString();
        String phone = phoneInput.getText().toString();
        String emergencyContact = emergencyContactInput.getText().toString();
        String allergies = allergiesInput.getText().toString();
        String bloodType = bloodTypeInput.getText().toString();
        String medicalConditions = medicalConditionsInput.getText().toString();
        String dateOfBirth = dateOfBirthInput.getText().toString();
        String sex = sexRadioGroup.getCheckedRadioButtonId() == R.id.radioMale ? "Male" : "Female";

        if (emergencyContact.isEmpty() || !emergencyContact.matches(".*\\d.*")) {
            Toast.makeText(this, "Please enter both name and phone number for emergency contact.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (userDatabase == null) return;

        userDatabase.child("fullName").setValue(name);
        userDatabase.child("phoneNumber").setValue(phone);
        userDatabase.child("emergencyContact").setValue(emergencyContact);
        userDatabase.child("allergies").setValue(allergies);
        userDatabase.child("bloodType").setValue(bloodType);
        userDatabase.child("medicalConditions").setValue(medicalConditions);
        userDatabase.child("dateOfBirth").setValue(dateOfBirth);
        userDatabase.child("sex").setValue(sex)
                .addOnSuccessListener(aVoid -> Toast.makeText(ProfileActivity21F21817.this, "Profile updated successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(ProfileActivity21F21817.this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
