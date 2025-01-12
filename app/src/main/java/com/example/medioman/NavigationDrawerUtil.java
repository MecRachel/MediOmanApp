package com.example.medioman;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class NavigationDrawerUtil {

    public static void setupNavigationDrawer(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar, NavigationView navigationView, boolean isGuest) {
        // Set up the drawer toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                activity, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            handleNavigationItemSelected(activity, item, drawerLayout, isGuest);
            return true;
        });

        if (isGuest) {
            Menu menu = navigationView.getMenu();
            MenuItem profileItem = menu.findItem(R.id.nav_item2);
            profileItem.setVisible(false);
        } else {
            Menu menu = navigationView.getMenu();
            MenuItem profileItem = menu.findItem(R.id.nav_item2);
            profileItem.setVisible(true);
        }
    }
    private static void handleNavigationItemSelected(Activity activity, MenuItem item, DrawerLayout drawerLayout, boolean isGuest) {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_item1) {
            if (isGuest) {
                Intent intent = new Intent(activity, UserDashboardActivity21F21817.class);
                intent.putExtra("isGuest", true);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
                activity.finish();
            } else {
                if (!(activity instanceof UserDashboardActivity21F21817)) {
                    Intent intent = new Intent(activity, UserDashboardActivity21F21817.class);
                    intent.putExtra("isGuest", false);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(intent);
                    activity.finish();
                }
            }
        } else if (itemId == R.id.nav_item2) { // Profile
            if (isGuest) {
                // Show the toast message for guests trying to access Profile
                Toast.makeText(activity, "Profile is only available for logged-in users.", Toast.LENGTH_SHORT).show();
            } else {
                activity.startActivity(new Intent(activity, ProfileActivity21F21817.class));
            }
        } else if (itemId == R.id.nav_item3) {
            if (!(activity instanceof EmergencyFirstAidActivity21F21817)) {
                activity.startActivity(new Intent(activity, EmergencyFirstAidActivity21F21817.class));
            }
        } else if (itemId == R.id.nav_item4) {
            activity.startActivity(new Intent(activity, FeedbackActivity21F21817.class));
        } else if (itemId == R.id.nav_logout) {
            if (!isGuest) {
                FirebaseAuth.getInstance().signOut();
            }
            activity.startActivity(new Intent(activity, LoginActivity21F21817.class));
            activity.finish();
        } else if (itemId == R.id.nav_about_us) {
            activity.startActivity(new Intent(activity, AboutUsActivity21F21817.class));
        } else if (itemId == R.id.nav_contact_us) {
            activity.startActivity(new Intent(activity, ContactUsActivity21F21817.class));
        } else if (itemId == R.id.nav_termsAndConditions) {
            activity.startActivity(new Intent(activity, TermsAndConditionsActivity.class));
        } else {
            Toast.makeText(activity, "Unknown item clicked.", Toast.LENGTH_SHORT).show();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
    }

}
