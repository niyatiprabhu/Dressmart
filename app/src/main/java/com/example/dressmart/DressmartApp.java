package com.example.dressmart;

import android.app.Application;

import com.example.dressmart.models.parse.Garment;
import com.example.dressmart.models.parse.OutfitPost;
import com.example.dressmart.models.parse.User;
import com.parse.Parse;
import com.parse.ParseObject;

public class DressmartApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(User.class);
        ParseObject.registerSubclass(Garment.class);
        ParseObject.registerSubclass(OutfitPost.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("qvTbnlwMPwwWFgeWeaqks2rl48J0PtbNjnqzkTqI")
                .clientKey("NZVyruoERwLmoD1LoJLFsNUk9jUo0aM3cvR7NZ4g")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }


}
