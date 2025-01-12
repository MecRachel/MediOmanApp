package com.example.medioman;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity21F21817 extends AppCompatActivity {

    private static final int SPLASH_TIMEOUT = 3000;
    private ImageView splashLogo;
    private TextView appNameText;
    private TextView versionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_medioman);

        splashLogo = findViewById(R.id.splashLogo);
        appNameText = findViewById(R.id.appNameText);
        versionText = findViewById(R.id.versionText);

        Animation topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_rachelanimation);
        Animation bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_rachelanimation);

        splashLogo.startAnimation(topAnimation);
        appNameText.startAnimation(bottomAnimation);
        versionText.startAnimation(bottomAnimation);

        View mainView = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            v.setPadding(insets.getInsets(WindowInsetsCompat.Type.systemBars()).left,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).right,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom);
            return insets;
        });
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(MainActivity21F21817.this, LoginActivity21F21817.class);
                startActivity(intent);
                finish();
            }, SPLASH_TIMEOUT);
        }
    }
