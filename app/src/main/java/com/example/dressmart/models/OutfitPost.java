package com.example.dressmart.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("OutfitPost")
public class OutfitPost extends ParseObject{

    public static final String KEY_AUTHOR = "author";
    public static final String KEY_LIKED_BY = "likedBy";
    public static final String KEY_GARMENTS = "garments";
    public static final String KEY_WEARING_OUTFIT_PICTURE = "wearingOutfitPicture";
    public static final String KEY_TEMPERATURE = "temperature";
    public static final String KEY_CONDITIONS = "conditions";

    public User getAuthor() {
        return (User) getParseUser(KEY_AUTHOR);

    }

    public void setAuthor(User user) {
        put(KEY_AUTHOR, user);
    }

    public List<User> getLikedBy() {
        List<User> likedBy = getList(KEY_LIKED_BY);
        if (likedBy == null) {
            return new ArrayList<>();
        }
        return likedBy;
    }

    public void setLikedBy(List<User> newLikedBy) {
        put(KEY_LIKED_BY, newLikedBy);
    }

    public List<Garment> getGarments() {
        List<Garment> garments = getList(KEY_GARMENTS);
        if (garments == null) {
            return new ArrayList<>();
        }
        return garments;
    }

    public void setGarments(List<Garment> newGarments) {
        put(KEY_GARMENTS, newGarments);
    }

    public ParseFile getWearingOutfitPicture() {
        return getParseFile(KEY_WEARING_OUTFIT_PICTURE);
    }

    public void setWearingOutfitPicture(ParseFile wearingOutfitPicture) {
        put(KEY_WEARING_OUTFIT_PICTURE, wearingOutfitPicture);
    }

    public int getTemperature() {
        return getInt(KEY_TEMPERATURE);
    }

    public String getConditions() {
        return getString(KEY_CONDITIONS);
    }



}