package com.example.dressmart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.dressmart.databinding.ActivitySignupBinding;
import com.example.dressmart.models.parse.User;
import com.example.dressmart.util.UserUtil;
import com.parse.ParseException;
import com.parse.SignUpCallback;

import java.util.ArrayList;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "Signup Activity";

   private ActivitySignupBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        binding.btnDoneSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = binding.etUsernameSignup.getText().toString();
                String password = binding.etPasswordSignup.getText().toString();
                String displayName = binding.etDisplayNameSignup.getText().toString();
                // TO DO: uploading profile picture
                signupUser(username, password, displayName);
            }
        });

    }

    private void signupUser(String username, String password, String displayName) {
        // Create the ParseUser
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setParseDisplayName(displayName);
        user.setParseOutfits(new ArrayList<>());
        user.setParseCloset(new ArrayList<>());
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    UserUtil.loginUser(username,password, SignupActivity.this);
                } else {
                    // Sign up didn't succeed.
                }
            }
        });
    }


}