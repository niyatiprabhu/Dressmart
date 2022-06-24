package com.example.dressmart.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("_User")
public class User extends ParseUser {

    public static final String KEY_OUTFITS = "outfits";
    public static final String KEY_DISPLAY_NAME = "displayName";
    public static final String KEY_PROFILE_PICTURE = "profilePicture";
    public static final String KEY_CLOSET = "closet";

    public User(){}

    public List<OutfitPost> getOutfits() {
        List<OutfitPost> outfits = getList(KEY_OUTFITS);
        if (outfits == null) {
            return new ArrayList<>();
        }
        return outfits;
    }

    public String getNumOutfits() {
        int numOutfits = getOutfits().size();
        return numOutfits + (numOutfits == 1 ? " Outfit" : " Outfits");
    }

    public void addOutfit(OutfitPost post) {
        List<OutfitPost> posts = getOutfits();
        posts.add(post);
        setOutfits(posts);
        saveInBackground();
    }

    public void setOutfits(List<OutfitPost> outfitPosts) {
        put(KEY_OUTFITS, outfitPosts);
    }

    public String getDisplayName() {
        return getString(KEY_DISPLAY_NAME);
    }

    public void setDisplayName(String newDisplayName) {
        put(KEY_DISPLAY_NAME, newDisplayName);
    }

    public ParseFile getProfilePicture() {
        return getParseFile(KEY_PROFILE_PICTURE);
    }

    public void setProfilePicture(ParseFile newProfilePicture) {
        put(KEY_PROFILE_PICTURE, newProfilePicture);
    }

    // getter and setter for closet
    public List<Garment> getCloset() {
        return getList(KEY_CLOSET);
    }

    public void setCloset(List<Garment> newCloset) {
        put(KEY_CLOSET, newCloset);
    }

}
