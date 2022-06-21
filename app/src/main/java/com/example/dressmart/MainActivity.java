package com.example.dressmart;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.dressmart.fragments.FeedFragment;
import com.example.dressmart.fragments.ProfileFragment;
import com.example.dressmart.fragments.TodayFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    FeedFragment feedFragment = new FeedFragment();
    ProfileFragment profileFragment = new ProfileFragment();
    TodayFragment todayFragment = new TodayFragment();

    public BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}