package com.example.medioman;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ReportsActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 100;
    private PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports_medioman);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        fetchUserSOSAlerts();
        Button userReportButton = findViewById(R.id.btnGenerateUserReport);
        Button guestReportButton = findViewById(R.id.btnGenerateGuestReport);
        pieChart = findViewById(R.id.pieChart);

        userReportButton.setOnClickListener(v -> generateUserSOSReport());
        guestReportButton.setOnClickListener(v -> generateGuestSOSReport());
    }
    private void fetchUserSOSAlerts() {
        DatabaseReference userSOSAlertsRef = FirebaseDatabase.getInstance().getReference("Users");

        userSOSAlertsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int maleCount = 0;
                int femaleCount = 0;
                int ageGroup1 = 0; // 18-30
                int ageGroup2 = 0; // 31-50
                int ageGroup3 = 0; // 51+

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String sex = userSnapshot.child("sex").getValue(String.class);
                    String dob = userSnapshot.child("dateOfBirth").getValue(String.class);

                    if (sex != null) {
                        if (sex.equalsIgnoreCase("male")) {
                            maleCount++;
                        } else if (sex.equalsIgnoreCase("female")) {
                            femaleCount++;
                        }
                    }
                    if (dob != null) {
                        int age = calculateAge(dob);
                        if (age >= 18 && age <= 30) {
                            ageGroup1++;
                        } else if (age >= 31 && age <= 50) {
                            ageGroup2++;
                        } else if (age >= 51) {
                            ageGroup3++;
                        }
                    }
                }
                displayPieChart(maleCount, femaleCount, ageGroup1, ageGroup2, ageGroup3);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ReportsActivity.this, "Failed to load user SOS alerts", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private int calculateAge(String dob) {
        try {
            String[] dobParts = dob.split("/");
            int birthDay = Integer.parseInt(dobParts[0]);
            int birthMonth = Integer.parseInt(dobParts[1]);
            int birthYear = Integer.parseInt(dobParts[2]);

            Calendar birthDate = Calendar.getInstance();
            birthDate.set(birthYear, birthMonth - 1, birthDay);

            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
            int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

            int age = currentYear - birthYear;

            if (currentMonth < birthMonth || (currentMonth == birthMonth && currentDay < birthDay)) {
                age--;
            }

            return age;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    private void displayPieChart(int maleCount, int femaleCount, int ageGroup1, int ageGroup2, int ageGroup3) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(maleCount, "Male"));
        entries.add(new PieEntry(femaleCount, "Female"));
        entries.add(new PieEntry(ageGroup1, "18-30"));
        entries.add(new PieEntry(ageGroup2, "31-50"));
        entries.add(new PieEntry(ageGroup3, "51+"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        PieData pieData = new PieData(dataSet);
        pieData.setValueTextSize(14f);
        pieData.setValueFormatter(new PercentFormatter());
        pieChart.setData(pieData);
        pieChart.setUsePercentValues(true);
        pieChart.invalidate();
        pieChart.getDescription().setText("User SOS Alerts");
        pieChart.getDescription().setTextSize(18f);
        pieChart.getDescription().setPosition(0.5f, 1.05f);

        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setFormSize(16f);
        legend.setTextSize(14f);
        legend.setTextColor(Color.BLACK);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);

        legend.setXEntrySpace(10f);
        legend.setYEntrySpace(5f);
    }
    public void generateUserSOSReport() {
        fetchUserSOSReports();
    }
    public void generateGuestSOSReport() {
        fetchGuestSOSReports();
    }
    private void fetchUserSOSReports() {
        DatabaseReference userSOSAlertsRef = FirebaseDatabase.getInstance().getReference("Users");

        userSOSAlertsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                StringBuilder reportContent = new StringBuilder("User SOS Alerts:\n\n");

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot alertSnapshot : userSnapshot.child("useralerts").getChildren()) {
                        String alertId = alertSnapshot.getKey();
                        String fullName = alertSnapshot.child("fullName").getValue(String.class);
                        String phoneNumber = alertSnapshot.child("phoneNumber").getValue(String.class);
                        String status = alertSnapshot.child("status").getValue(String.class); // Fetch status
                        String eta = alertSnapshot.child("eta").getValue(String.class); // Fetch ETA

                        Double latitude = alertSnapshot.child("latitude").getValue(Double.class);
                        Double longitude = alertSnapshot.child("longitude").getValue(Double.class);

                        String location = "Not available";
                        if (latitude != null && longitude != null) {
                            location = "Latitude: " + latitude + ", Longitude: " + longitude;
                        }
                        reportContent.append("Alert ID: ").append(alertId)
                                .append("\nFull Name: ").append(fullName)
                                .append("\nPhone: ").append(phoneNumber)
                                .append("\nLocation: ").append(location)
                                .append("\nStatus: ").append(status != null ? status : "Not provided") // Add status to report
                                .append("\nETA: ").append(eta != null ? eta : "Not provided") // Add ETA to report
                                .append("\n\n");
                    }
                }
                generatePDF(reportContent.toString(), "User_SOS_Report.pdf");
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ReportsActivity.this, "Failed to load user SOS alerts", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void fetchGuestSOSReports() {
        DatabaseReference guestSOSAlertsRef = FirebaseDatabase.getInstance().getReference("GuestSOSAlerts");
        guestSOSAlertsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                StringBuilder reportContent = new StringBuilder("Guest SOS Alerts:\n\n");
                for (DataSnapshot guestSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot alertSnapshot : guestSnapshot.child("guestalerts").getChildren()) {
                        String alertId = alertSnapshot.getKey();
                        String fullName = alertSnapshot.child("fullName").getValue(String.class);
                        String formattedTimestamp = alertSnapshot.child("formattedTimestamp").getValue(String.class);
                        String status = alertSnapshot.child("status").getValue(String.class); // Fetch status
                        String eta = alertSnapshot.child("eta").getValue(String.class); // Fetch ETA
                        Double latitude = alertSnapshot.child("latitude").getValue(Double.class);
                        Double longitude = alertSnapshot.child("longitude").getValue(Double.class);

                        // Handle null values and type mismatches gracefully
                        if (fullName == null) fullName = "Unknown";
                        if (formattedTimestamp == null) formattedTimestamp = "Unknown time";
                        if (latitude == null || longitude == null) {
                            reportContent.append("Alert ID: ").append(alertId)
                                    .append("\nFull Name: ").append(fullName)
                                    .append("\nTimestamp: ").append(formattedTimestamp)
                                    .append("\nLocation: Not available")
                                    .append("\nStatus: ").append(status != null ? status : "Not provided")
                                    .append("\nETA: ").append(eta != null ? eta : "Not provided")
                                    .append("\n\n");
                        } else {
                            String location = "Latitude: " + latitude + ", Longitude: " + longitude;
                            reportContent.append("Alert ID: ").append(alertId)
                                    .append("\nFull Name: ").append(fullName)
                                    .append("\nTimestamp: ").append(formattedTimestamp)
                                    .append("\nLocation: ").append(location)
                                    .append("\nStatus: ").append(status != null ? status : "Not provided")
                                    .append("\nETA: ").append(eta != null ? eta : "Not provided")
                                    .append("\n\n");
                        }
                    }
                }
                generatePDF(reportContent.toString(), "Guest_SOS_Report.pdf");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ReportsActivity.this, "Failed to load guest SOS alerts", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void generatePDF(String reportContent, String fileName) {
        try {
            String timestamp = String.valueOf(System.currentTimeMillis());
            String uniqueFileName = fileName.replace(".pdf", "_" + timestamp + ".pdf");
            File downloadsDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "SOSReports");
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs();
            }
            File pdfFile = new File(downloadsDir, uniqueFileName);
            Log.d("ReportsActivity", "Generating PDF at: " + pdfFile.getAbsolutePath());

            PdfWriter writer = new PdfWriter(new FileOutputStream(pdfFile));
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            document.add(new Paragraph("SOS ALERT REPORT")
                    .setBold()
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph("Generated at: " + getCurrentDateTime())
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("\n"));
            document.add(new Paragraph(reportContent));
            document.close();
            Log.d("ReportsActivity", "PDF generated successfully!");
            Toast.makeText(this, "Report generated successfully!", Toast.LENGTH_SHORT).show();
            openPDF(pdfFile);

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("ReportsActivity", "Failed to generate the report: " + e.getMessage());
            Toast.makeText(this, "Failed to generate the report", Toast.LENGTH_SHORT).show();
        }
    }
    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
    private Cell createCell(String content, boolean isHeader) {
        Cell cell = new Cell().add(new Paragraph(content).setFontSize(isHeader ? 12 : 10));
        if (isHeader) {
            cell.setBold()
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER);
        } else {
            cell.setTextAlignment(TextAlignment.LEFT);
        }
        return cell;
    }
    private void openPDF(File pdfFile) {
        if (pdfFile.exists()) {
            Log.d("ReportsActivity", "Opening PDF: " + pdfFile.getAbsolutePath());

            Uri pdfUri = Uri.fromFile(pdfFile);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(pdfUri, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Log.e("ReportsActivity", "No PDF viewer found: " + e.getMessage());
                Toast.makeText(this, "No PDF viewer found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("ReportsActivity", "PDF file not found: " + pdfFile.getAbsolutePath());
            Toast.makeText(this, "PDF file not found", Toast.LENGTH_SHORT).show();
        }
    }
}
