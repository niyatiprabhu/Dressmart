package com.example.dressmart.models.parse;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

/**
 * Parse class to model a garment object, which make up an OutfitPost
  */
@ParseClassName("Garment")
public class Garment extends ParseObject {

    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_GARMENT_TYPE = "garmentType";
    public static final String KEY_SUBTYPE = "subType";
    public static final String KEY_GARMENT_PICTURE = "garmentPicture";

    public Garment(){}

    public Garment(String description, String garmentType, String subtype) {
        setParseDescription(description);
        setParseGarmentType(garmentType);
        setParseSubtype(subtype);
    }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setParseDescription(String newDescription) {
        put(KEY_DESCRIPTION, newDescription);
        saveInBackground();
    }

    public String getGarmentType() throws ParseException {
        return fetchIfNeeded().getString(KEY_GARMENT_TYPE);
    }

    public void setParseGarmentType(String newType) {
        put(KEY_GARMENT_TYPE, newType);
        saveInBackground();
    }

    public String getSubtype() {
        return getString(KEY_SUBTYPE);
    }

    public void setParseSubtype(String newType) {
        put(KEY_SUBTYPE, newType);
        saveInBackground();

    }

    public ParseFile getGarmentPicture() {
        return getParseFile(KEY_GARMENT_PICTURE);
    }

    public void setParseGarmentPicture(ParseFile newPicture) {
        put(KEY_GARMENT_PICTURE, newPicture);
        saveInBackground();
    }

}
