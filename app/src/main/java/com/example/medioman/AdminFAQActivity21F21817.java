package com.example.medioman;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AdminFAQActivity21F21817 extends AppCompatActivity {

    private TextView faqAnswer1, faqAnswer2, faqAnswer3, faqAnswer4, faqAnswer5, faqAnswer6;
    private ImageButton arrow1, arrow2, arrow3, arrow4, arrow5, arrow6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_faq_medioman);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        faqAnswer1 = findViewById(R.id.answer1);
        faqAnswer2 = findViewById(R.id.answer2);
        faqAnswer3 = findViewById(R.id.answer3);
        faqAnswer4 = findViewById(R.id.answer4);
        faqAnswer5 = findViewById(R.id.answer5);
        faqAnswer6 = findViewById(R.id.answer6);

        arrow1 = findViewById(R.id.arrow1);
        arrow2 = findViewById(R.id.arrow2);
        arrow3 = findViewById(R.id.arrow3);
        arrow4 = findViewById(R.id.arrow4);
        arrow5 = findViewById(R.id.arrow5);
        arrow6 = findViewById(R.id.arrow6);

        faqAnswer1.setVisibility(View.GONE);
        faqAnswer2.setVisibility(View.GONE);
        faqAnswer3.setVisibility(View.GONE);
        faqAnswer4.setVisibility(View.GONE);
        faqAnswer5.setVisibility(View.GONE);
        faqAnswer6.setVisibility(View.GONE);

        arrow1.setOnClickListener(v -> toggleAnswerVisibility(faqAnswer1));
        arrow2.setOnClickListener(v -> toggleAnswerVisibility(faqAnswer2));
        arrow3.setOnClickListener(v -> toggleAnswerVisibility(faqAnswer3));
        arrow4.setOnClickListener(v -> toggleAnswerVisibility(faqAnswer4));
        arrow5.setOnClickListener(v -> toggleAnswerVisibility(faqAnswer5));
        arrow6.setOnClickListener(v -> toggleAnswerVisibility(faqAnswer6));
    }
    private void toggleAnswerVisibility(TextView answer) {
        if (answer.getVisibility() == View.VISIBLE) {
            answer.setVisibility(View.GONE);
        } else {
            answer.setVisibility(View.VISIBLE);
        }
    }
}
