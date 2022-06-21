package com.example.dressmart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.dressmart.models.User;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.ArrayList;

public class SignupActivity extends AppCompatActivity {

    public static final String TAG = "Signup Activity";

    EditText etUsernameSignup;
    EditText etPasswordSignup;
    EditText etDisplayNameSignup;
    ImageButton ibUploadProfilePictureSignup;
    Button btnDoneSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etUsernameSignup = findViewById(R.id.etUsernameSignup);
        etPasswordSignup = findViewById(R.id.etPasswordSignup);
        etDisplayNameSignup = findViewById(R.id.etDisplayNameSignup);
        ibUploadProfilePictureSignup = findViewById(R.id.ibUploadProfilePicSignup);
        btnDoneSignup = findViewById(R.id.btnDoneSignup);

        btnDoneSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsernameSignup.getText().toString();
                String password = etPasswordSignup.getText().toString();
                String displayName = etDisplayNameSignup.getText().toString();
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
        user.setDisplayName(displayName);
        user.setOutfits(new ArrayList<>());
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    loginUser(username,password);
                } else {
                    // Sign up didn't succeed.
                }
            }
        });
    }

    private void loginUser(String username, String password) {
        Log.i(TAG, "Attempting to login user " + username);
        // Navigate to the main activity if correct
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    // something went wrong
                    Log.e(TAG, "Issue with login", e);
                    return;
                }
                goMainActivity();
                Toast.makeText(SignupActivity.this, "Success!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        // don't want to come back to login screen when click back
        finish();
    }
}