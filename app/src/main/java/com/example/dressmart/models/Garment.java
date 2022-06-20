package com.example.dressmart.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

@ParseClassName("Garment")
public class Garment extends ParseObject {

    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_GARMENT_TYPE = "garmentType";
    public static final String KEY_SUBTYPE = "subType";
    public static final String KEY_GARMENT_PICTURE = "garmentPicture";
    public static final String KEY_OWNER = "owner";

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String newDescription) {
        put(KEY_DESCRIPTION, newDescription);
    }

    public String getGarmentType() {
        return getString(KEY_GARMENT_TYPE);
    }

    public void setGarmentType(String newType) {
        put(KEY_GARMENT_TYPE, newType);
    }

    public String getSubtype() {
        return getString(KEY_SUBTYPE);
    }

    public void setSubtype(String newType) {
        put(KEY_SUBTYPE, newType);
    }

    public ParseFile getGarmentPicture() {
        return getParseFile(KEY_GARMENT_PICTURE);
    }

    public void setGarmentPicture(ParseFile newPicture) {
        put(KEY_GARMENT_PICTURE, newPicture);
    }

    public User getOwner() {
        return (User) getParseUser(KEY_OWNER);
    }
}
