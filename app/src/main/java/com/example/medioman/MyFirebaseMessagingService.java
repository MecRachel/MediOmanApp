package com.example.medioman;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCM Service";
    private static final String CHANNEL_ID = "sos_alert_channel";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            String fullName = remoteMessage.getData().get("fullName");
            String latitude = remoteMessage.getData().get("latitude");
            String longitude = remoteMessage.getData().get("longitude");
            String phoneNumber = remoteMessage.getData().get("phoneNumber");

            SOSAlertActivity21F21817.SOSAlert sosAlert = new SOSAlertActivity21F21817.SOSAlert();
            sosAlert.fullName = fullName;
            sosAlert.latitude = Double.parseDouble(latitude);
            sosAlert.longitude = Double.parseDouble(longitude);
            sosAlert.phoneNumber = phoneNumber;

            AdminDashboardActivity dashboardActivity = new AdminDashboardActivity();
            dashboardActivity.showNotificationWithRingtone(sosAlert);
        }
    }
    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, UserDashboardActivity21F21817.class); // Change to the appropriate activity
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // Ensure you have this drawable
                .setContentTitle("SOS Alert")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "SOS Alerts", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(0, notificationBuilder.build());
    }
}
