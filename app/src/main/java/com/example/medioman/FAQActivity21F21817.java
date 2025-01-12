package com.example.medioman;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class FAQActivity21F21817 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_faq_medioman);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        setupFAQToggle(R.id.arrow1, R.id.answer1);
        setupFAQToggle(R.id.arrow2, R.id.answer2);
        setupFAQToggle(R.id.arrow3, R.id.answer3);
        setupFAQToggle(R.id.arrow4, R.id.answer4);
        setupFAQToggle(R.id.arrow5, R.id.answer5);
        setupFAQToggle(R.id.arrow6, R.id.answer6);
        setupFAQToggle(R.id.arrow7, R.id.answer7);
    }
    private void setupFAQToggle(int arrowId, int answerId) {
        ImageButton arrowButton = findViewById(arrowId);
        TextView answerText = findViewById(answerId);

        arrowButton.setOnClickListener(v -> {
            if (answerText.getVisibility() == View.GONE) {
                answerText.setVisibility(View.VISIBLE);
                arrowButton.setImageResource(R.drawable.ic_arrow_up);
            } else {
                answerText.setVisibility(View.GONE);
                arrowButton.setImageResource(R.drawable.ic_arrow_down);
            }
        });
    }
}
