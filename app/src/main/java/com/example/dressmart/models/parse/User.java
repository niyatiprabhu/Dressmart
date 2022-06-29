package com.example.dressmart.models.parse;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 *  Parse class that models a user on Dressmart
 */
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


    public void addParseOutfit(OutfitPost post) {
        List<OutfitPost> posts = getOutfits();
        posts.add(post);
        setParseOutfits(posts);
    }

    public void setParseOutfits(List<OutfitPost> outfitPosts) {
        put(KEY_OUTFITS, outfitPosts);
    }

    public String getDisplayName() {
        return getString(KEY_DISPLAY_NAME);
    }

    public void setParseDisplayName(String newDisplayName) {
        put(KEY_DISPLAY_NAME, newDisplayName);
    }

    public ParseFile getProfilePicture() {
        return getParseFile(KEY_PROFILE_PICTURE);
    }

    public void setParseProfilePicture(ParseFile newProfilePicture) {
        put(KEY_PROFILE_PICTURE, newProfilePicture);
    }

    // getter and setter for closet
    public List<Garment> getCloset() {
        return getList(KEY_CLOSET);
    }

    public void setParseCloset(List<Garment> newCloset) {
        put(KEY_CLOSET, newCloset);
    }

    public String getNumOutfits() {
        int numOutfits = getOutfits().size();
        return numOutfits == 1 ? numOutfits + " Outfit" : numOutfits + " Outfits";
    }
}
