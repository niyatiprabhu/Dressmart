package com.example.dressmart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.dressmart.databinding.ActivitySignupBinding;
import com.example.dressmart.databinding.ActivitySplashScreenBinding;

public class SplashScreenActivity extends AppCompatActivity {

    Animation fade;
    ActivitySplashScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        fade = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        fade.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        binding.ivLogoSplash.startAnimation(fade);
    }
}