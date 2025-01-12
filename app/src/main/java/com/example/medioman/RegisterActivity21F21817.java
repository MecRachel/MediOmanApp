package com.example.medioman;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class RegisterActivity21F21817 extends AppCompatActivity {

    private EditText fullNameInput, emailInput, passwordInput, phoneNumberInput;
    private EditText emergencyContactInput, allergiesInput, medicalConditionsInput;
    private EditText dateOfBirthInput;
    private Spinner bloodTypeInput;
    private RadioGroup sexRadioGroup;
    private Button registerButton;
    private ImageView togglePasswordVisibility;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private CheckBox termsCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_medioman);

        fullNameInput = findViewById(R.id.fullNameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        togglePasswordVisibility = findViewById(R.id.togglePasswordVisibility);
        phoneNumberInput = findViewById(R.id.phoneNumberInput);
        emergencyContactInput = findViewById(R.id.emergencyContactInput);
        allergiesInput = findViewById(R.id.allergiesInput);
        bloodTypeInput = findViewById(R.id.bloodTypeInput);
        medicalConditionsInput = findViewById(R.id.medicalConditionsInput);
        dateOfBirthInput = findViewById(R.id.dateOfBirthInput);
        sexRadioGroup = findViewById(R.id.sexRadioGroup);
        registerButton = findViewById(R.id.registerButton);
        termsCheckbox = findViewById(R.id.termsCheckbox);
        TextView termsLink = findViewById(R.id.termsLink);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        dateOfBirthInput.setOnClickListener(v -> showDatePickerDialog());

        registerButton.setOnClickListener(view -> registerUser());

        togglePasswordVisibility.setOnClickListener(view -> togglePasswordVisibility());
        passwordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
        togglePasswordVisibility.setImageResource(R.drawable.ic_visibility_off_medioman);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.blood_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        bloodTypeInput.setAdapter(adapter);

        ImageButton backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity21F21817.this, LoginActivity21F21817.class);
            startActivity(intent);
            finish();
        });

        termsLink.setOnClickListener(v -> {
            Intent termsIntent = new Intent(RegisterActivity21F21817.this, TermsAndConditionsActivity.class);
            termsIntent.putExtra("fromRegisterOrGuest", true);
            startActivity(termsIntent);
        });
    }
    private void togglePasswordVisibility() {
        if (passwordInput.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
            passwordInput.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            togglePasswordVisibility.setImageResource(R.drawable.ic_visibility_on_medioman);
        } else {
            passwordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
            togglePasswordVisibility.setImageResource(R.drawable.ic_visibility_off_medioman);
        }
        passwordInput.setSelection(passwordInput.getText().length());
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
    private void registerUser() {
        if (!termsCheckbox.isChecked()) {
            Toast.makeText(this, "You must accept the Terms and Conditions to proceed.", Toast.LENGTH_SHORT).show();
            return;
        }
        String fullName = fullNameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String phoneNumber = phoneNumberInput.getText().toString().trim();
        String emergencyContact = emergencyContactInput.getText().toString().trim();
        String allergies = allergiesInput.getText().toString().trim();
        String bloodType = bloodTypeInput.getSelectedItem().toString().trim();
        String medicalConditions = medicalConditionsInput.getText().toString().trim();
        String dateOfBirth = dateOfBirthInput.getText().toString().trim();
        int selectedSexId = sexRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedSexButton = findViewById(selectedSexId);
        String sex = selectedSexButton != null ? selectedSexButton.getText().toString().trim() : "";

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || phoneNumber.isEmpty() || dateOfBirth.isEmpty() || sex.isEmpty()) {
            Toast.makeText(this, "Please fill all the required fields", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = mAuth.getCurrentUser().getUid();
                        User user = new User(fullName, email, phoneNumber, emergencyContact, allergies, bloodType, medicalConditions, dateOfBirth, sex);

                        databaseReference.child(userId).setValue(user)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Toast.makeText(RegisterActivity21F21817.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(RegisterActivity21F21817.this, LoginActivity21F21817.class));
                                        finish();
                                    } else {
                                        Toast.makeText(RegisterActivity21F21817.this, "Failed to save user info: " + task1.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                    } else {
                        Toast.makeText(RegisterActivity21F21817.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
    public static class User {
        public String fullName;
        public String email;
        public String phoneNumber;
        public String emergencyContact;
        public String allergies;
        public String bloodType;
        public String medicalConditions;
        public String dateOfBirth;
        public String sex;
        public User() {}
        public User(String fullName, String email, String phoneNumber, String emergencyContact, String allergies, String bloodType, String medicalConditions, String dateOfBirth, String sex) {
            this.fullName = fullName;
            this.email = email;
            this.phoneNumber = phoneNumber;
            this.emergencyContact = emergencyContact;
            this.allergies = allergies;
            this.bloodType = bloodType;
            this.medicalConditions = medicalConditions;
            this.dateOfBirth = dateOfBirth;
            this.sex = sex;
        }
    }
}
