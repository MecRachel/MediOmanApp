package com.example.medioman;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
public class SOSAlertAdapter21F21817 extends RecyclerView.Adapter<SOSAlertAdapter21F21817.ViewHolder> {

    private List<SOSAlertActivity21F21817.SOSAlert> sosAlerts;
    private OnAlertSelectedListener selectedListener;
    private OnCancelAlertListener cancelListener;
    private boolean showCancelButton;
    public SOSAlertAdapter21F21817(List<SOSAlertActivity21F21817.SOSAlert> sosAlerts,
                                   OnAlertSelectedListener selectedListener,
                                   OnCancelAlertListener cancelListener,
                                   boolean showCancelButton) {
        this.sosAlerts = sosAlerts;
        this.selectedListener = selectedListener;
        this.cancelListener = cancelListener;
        this.showCancelButton = showCancelButton;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sos_alert_item_medioman, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SOSAlertActivity21F21817.SOSAlert sosAlert = sosAlerts.get(position);
        holder.bind(sosAlert, selectedListener, cancelListener, showCancelButton);
    }

    @Override
    public int getItemCount() {
        return sosAlerts.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView alertInfo;
        private TextView alertTime;
        private TextView alertStatus;
        private TextView etaTextView;
        private Button cancelAlertButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            alertInfo = itemView.findViewById(R.id.alertInfo);
            alertTime = itemView.findViewById(R.id.alertTime);
            alertStatus = itemView.findViewById(R.id.alertStatus);
            etaTextView = itemView.findViewById(R.id.etaTextView);
            cancelAlertButton = itemView.findViewById(R.id.cancelAlertButton);
        }
        public void bind(SOSAlertActivity21F21817.SOSAlert sosAlert,
                         OnAlertSelectedListener selectedListener,
                         OnCancelAlertListener cancelListener,
                         boolean showCancelButton) {
            String fullName = sosAlert.fullName != null ? sosAlert.fullName : "Guest User";
            String status = sosAlert.status != null ? sosAlert.status : "Not dispatched";

            String location = (sosAlert.latitude != 0 && sosAlert.longitude != 0) ?
                    sosAlert.latitude + ", " + sosAlert.longitude : "Location not available";
            alertInfo.setText("SOS from: " + fullName + "\nLocation: " + location);

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            String formattedTime = sdf.format(new Date(sosAlert.timestamp != 0 ? sosAlert.timestamp : System.currentTimeMillis()));
            alertTime.setText("Received at: " + formattedTime);

            alertStatus.setText("Status: " + status);

            if (sosAlert.eta != null && !sosAlert.eta.isEmpty()) {
                etaTextView.setText("ETA: " + sosAlert.eta);
                etaTextView.setVisibility(View.VISIBLE);
            } else {
                etaTextView.setVisibility(View.GONE);
            }
            itemView.setOnClickListener(v -> selectedListener.onAlertSelected(sosAlert));
            if (showCancelButton && "Pending".equals(sosAlert.status)) {
                cancelAlertButton.setVisibility(View.VISIBLE);
                cancelAlertButton.setOnClickListener(v -> cancelListener.onCancelAlert(sosAlert));
            } else {
                cancelAlertButton.setVisibility(View.GONE);
            }
        }
    }
    public interface OnAlertSelectedListener {
        void onAlertSelected(SOSAlertActivity21F21817.SOSAlert sosAlert);
    }
    public interface OnCancelAlertListener {
        void onCancelAlert(SOSAlertActivity21F21817.SOSAlert sosAlert);
    }
}
