package com.example.medioman;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import android.widget.TextView;

public class ContactUsActivity21F21817 extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private boolean isGuest = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us_medioman);

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
        TextView contactUsTextView = findViewById(R.id.contactUsTextView);
        contactUsTextView.setText(getContactUsContent());
    }

    private String getContactUsContent() {
        return "For any inquiries, feedback, or support, please reach out to us:\n\n" +
                "Email: support@medioman.com\n" +
                "Phone: +968 9854 5678\n" +
                "Address: MediOman, Muscat, Oman\n" +
                "We are here to assist you!";
    }
}
