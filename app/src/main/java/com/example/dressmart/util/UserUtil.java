package com.example.dressmart.util;

import android.util.Log;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class UserUtil {

    private static final String TAG = "User Util";

    public static void loginUser(String username, String password) {
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
            }
        });
    }
}
