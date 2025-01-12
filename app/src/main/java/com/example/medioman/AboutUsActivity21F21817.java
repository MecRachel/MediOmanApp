package com.example.medioman;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class AboutUsActivity21F21817 extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private boolean isGuest = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us_medioman);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            isGuest = true;
        } else {
            isGuest = false;
        }
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        NavigationDrawerUtil.setupNavigationDrawer(this, drawerLayout, toolbar, navigationView, isGuest);
        loadAboutUsContent();
    }
    private void loadAboutUsContent() {
        String aboutUsContent1 = "MediOman is an innovative emergency ambulance service application that leverages GPS technology to enhance response times and efficiency in Oman. With features like real-time location tracking and immediate SOS alerts, the app ensures rapid ambulance deployment during emergencies.";
        String aboutUsContent2 = "In today's fast-paced society, access to immediate medical care is crucial, especially in life-threatening situations. MediOman was created to assist Omani citizens, tourists, and expatriates by changing how emergency healthcare services are procured and administered.";
        String aboutUsContent3 = "MediOman acts as a lifeline in times of need by connecting individuals quickly to hospital administrators. Users can notify the nearest hospital about an emergency with just a touch, triggering a rapid response from ambulance operators.";
        String aboutUsContent4 = "The app's feedback system fosters community participation and allows users to share their experiences, contributing to continuous improvement. MediOman reflects a dedication to the safety and wellness of individuals in Oman, aligning with the country’s goal of providing effective and prompt healthcare access.";
        TextView sectionContent1 = findViewById(R.id.sectionContent1);
        sectionContent1.setText(aboutUsContent1);
        TextView sectionContent2 = findViewById(R.id.sectionContent2);
        sectionContent2.setText(aboutUsContent2);
        TextView sectionContent3 = findViewById(R.id.sectionContent3);
        sectionContent3.setText(aboutUsContent3);
        TextView sectionContent4 = findViewById(R.id.sectionContent4);
        sectionContent4.setText(aboutUsContent4);
    }
}
