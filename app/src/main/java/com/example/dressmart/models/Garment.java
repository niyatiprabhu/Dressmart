package com.example.dressmart.models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Garment")
public class Garment extends ParseObject {

    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_GARMENT_TYPE = "garmentType";
    public static final String KEY_SUBTYPE = "subType";
    public static final String KEY_GARMENT_PICTURE = "garmentPicture";

    public Garment(){}

    public Garment(String description, String garmentType, String subtype) {
        setDescription(description);
        setGarmentType(garmentType);
        setSubtype(subtype);
    }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String newDescription) {
        put(KEY_DESCRIPTION, newDescription);
    }

    public String getGarmentType() throws ParseException {
        return fetchIfNeeded().getString(KEY_GARMENT_TYPE);
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

}
