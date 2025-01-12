package com.example.medioman;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class TermsAndConditionsActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private boolean isGuest = false;
    private boolean fromRegisterOrGuest = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions);

        fromRegisterOrGuest = getIntent().getBooleanExtra("fromRegisterOrGuest", false);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            isGuest = true;
        } else {
            isGuest = false;
        }
        toolbar = findViewById(R.id.toolbar);

        if (fromRegisterOrGuest) {
            toolbar.setVisibility(View.GONE);
            drawerLayout = findViewById(R.id.drawer_layout);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else {
            setSupportActionBar(toolbar);
            drawerLayout = findViewById(R.id.drawer_layout);
            navigationView = findViewById(R.id.nav_view);
            NavigationDrawerUtil.setupNavigationDrawer(this, drawerLayout, toolbar, navigationView, isGuest);
        }
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        TextView termsContent = findViewById(R.id.termsContent);
        String termsText = getTermsAndConditionsText();
        termsContent.setText(termsText);
    }
    private String getTermsAndConditionsText() {
        return "1. Introduction\n" +
                "   - MediOman is a prototype application under development, designed to provide rapid-response emergency ambulance services. The app is subject to updates, improvements, and adjustments as required by applicable laws and regulations, particularly in the Sultanate of Oman.\n\n" +
                "2. User Data Collection\n" +
                "   - The App collects data related to your account registration, login credentials, and user activity within the application.\n" +
                "   - Collected data is stored securely in Firebase and may include: name, email address, profile information, preferences, device information, and interactions with the app.\n\n" +
                "3. Usage of the App\n" +
                "   - The app is intended to provide emergency response services but is still evolving with new features and functionality.\n" +
                "   - Users and Guest Users will need an active internet connection (Wi-Fi or mobile data) to access key features such as SOS alerts, Location tracking, and communication with hospital admins for ETA.\n\n" +
                "4. Guest Access\n" +
                "   - Guest users can use the app with limited functionality.\n" +
                "   - Guest users' interactions may be logged, but they will not be associated with a registered account.\n\n" +
                "5. Limitations of Liability\n" +
                "   - MediOman is designed to assist with emergency services but does not guarantee uninterrupted or error-free operation.\n" +
                "   - The app's features and functionalities are subject to ongoing development and may be updated, modified, or temporarily unavailable without prior notice.\n\n" +
                "6. Terms and Conditions Updates\n" +
                "   - These Terms and Conditions are subject to change. t is the user's responsibility to review them regularly to stay informed about any changes.\n\n" +
                "7. Compliance with Omani Law\n" +
                "   - As the App operates in Oman, it will comply with the applicable laws and regulations of the Sultanate of Oman. Any updates or changes to the App will follow these legal requirements.\n\n" +
                "8. Prototype Status\n" +
                "   - The App is in a prototype stage and not fully developed. It is subject to continuous development, improvements, and changes.\n" +
                "   - Feedback from users is welcomed and may influence future versions of the App.\n\n" +
                "9. Data Security\n" +
                "   - While reasonable measures are taken to protect your data, absolute security cannot be guaranteed.\n" +
                "   - Users are encouraged to report any security concerns immediately.\n\n" +
                "10. Acceptance of Terms\n" +
                "   - By using MediOman, you acknowledge and accept these Terms and Conditions.\n";
    }
}
