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
    public static final String KEY_WEARING_OUTFIT_PICTURE = "wearingOutfitPicture";
    public static final String KEY_TEMPERATURE = "temperature";
    public static final String KEY_CONDITIONS = "conditions";
    public static final String KEY_TOP = "top";
    public static final String KEY_BOTTOMS = "bottoms";
    public static final String KEY_OUTER = "outer";
    public static final String KEY_SHOES = "shoes";

    public OutfitPost(){}


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

    public Garment getTop() {
        return (Garment) get(KEY_TOP);
    }

    public void setParseTop(Garment top) {
        put(KEY_TOP, top);
    }

    public Garment getBottoms() {
        return (Garment) get(KEY_BOTTOMS);
    }

    public void setParseBottoms(Garment bottoms) {
        put(KEY_BOTTOMS, bottoms);
    }

    public Garment getOuter() {
        return (Garment) get(KEY_OUTER);
    }

    public void setParseOuter(Garment outer) {
        if (outer != null) {
            put(KEY_OUTER, outer);
        }
    }

    public Garment getShoes() {
        return (Garment) get(KEY_SHOES);
    }

    public void setParseShoes(Garment shoes) {
        put(KEY_SHOES, shoes);
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
