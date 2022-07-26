package com.example.dressmart.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.dressmart.MainActivity;
import com.example.dressmart.R;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class UserUtil {

    private static final String TAG = "User Util";

    public static void loginUser(String username, String password, Activity activity) {
        Log.i(TAG, "Attempting to login user " + username);
        // Navigate to the main activity if correct
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    // something went wrong
                    Log.e(TAG, "Issue with login", e);
                    EditText etUsername = activity.findViewById(R.id.etUsername);
                    EditText etPassword = activity.findViewById(R.id.etPassword);
                    Animation shake = AnimationUtils.loadAnimation(activity, R.anim.shake);
                    etUsername.startAnimation(shake);
                    etUsername.getText().clear();
                    etPassword.startAnimation(shake);
                    etPassword.getText().clear();
                    Snackbar.make(activity.findViewById(R.id.clLoginView), activity.getString(R.string.message_wrong_password), BaseTransientBottomBar.LENGTH_LONG).show();
                    return;
                }
                goMainActivity(activity);
            }
        });
    }

    public static void goMainActivity(Activity activity) {
        Intent i = new Intent(activity, MainActivity.class);
        activity.startActivity(i);
        // don't want to come back to login screen when click back
        activity.finish();
    }

}
