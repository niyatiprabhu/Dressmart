package com.example.dressmart.models.parse;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Parse class to model a post that exists on profile and main feed
 */
@ParseClassName("OutfitPost")
public class OutfitPost extends ParseObject{

    public static final String KEY_AUTHOR = "author";
    public static final String KEY_LIKED_BY = "likedBy";
    public static final String KEY_GARMENTS = "garments";
    public static final String KEY_WEARING_OUTFIT_PICTURE = "wearingOutfitPicture";
    public static final String KEY_TEMPERATURE = "temperature";
    public static final String KEY_CONDITIONS = "conditions";

    public OutfitPost(){}

    public OutfitPost(User currentUser, ArrayList<User> likedBy, List<Garment> garments, int temp, String conditions) {
        setParseAuthor(currentUser);
        setParseLikedBy(likedBy);
        setParseGarments(garments);
        put(KEY_TEMPERATURE, temp);
        put(KEY_CONDITIONS, conditions);
    }

    public User getAuthor() {
        return (User) getParseUser(KEY_AUTHOR);

    }

    public void setParseAuthor(User user) {
        put(KEY_AUTHOR, user);
    }

    public List<User> getLikedBy() {
        List<User> likedBy = getList(KEY_LIKED_BY);
        if (likedBy == null) {
            return new ArrayList<>();
        }
        return likedBy;
    }

    public void setParseLikedBy(List<User> newLikedBy) {
        put(KEY_LIKED_BY, newLikedBy);
    }

    public List<Garment> getGarments() {
        List<Garment> garments = getList(KEY_GARMENTS);
        if (garments == null) {
            return new ArrayList<>();
        }
        return garments;
    }

    public void setParseGarments(List<Garment> newGarments) {
        put(KEY_GARMENTS, newGarments);
    }

    public ParseFile getWearingOutfitPicture() {
        return getParseFile(KEY_WEARING_OUTFIT_PICTURE);
    }

    public void setParseWearingOutfitPicture(ParseFile wearingOutfitPicture) {
        put(KEY_WEARING_OUTFIT_PICTURE, wearingOutfitPicture);
    }

    public int getTemperature() {
        return getInt(KEY_TEMPERATURE);
    }

    public void setParseTemperature(int temp) {
        put(KEY_TEMPERATURE, temp);
    }


    public String getConditions() {
        return getString(KEY_CONDITIONS);
    }

    public void setParseConditions(String conditions) {
        put(KEY_CONDITIONS, conditions);
    }
}
