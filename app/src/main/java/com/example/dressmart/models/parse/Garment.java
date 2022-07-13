package com.example.dressmart.models.parse;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.util.Date;

/**
 * Parse class to model a garment object, which make up an OutfitPost
  */
@ParseClassName("Garment")
public class Garment extends ParseObject implements Comparable<Garment>{

    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_GARMENT_TYPE = "garmentType";
    public static final String KEY_SUBTYPE = "subType";
    public static final String KEY_GARMENT_PICTURE = "garmentPicture";
    public static final String KEY_DATE_LAST_WORN = "dateLastWorn";
    public static final String KEY_OWNER = "owner";

    public Garment(){}

    public User getOwner() {
        return (User) getParseUser(KEY_OWNER);
    }

    public void setOwner(User user) {
        put(KEY_OWNER, user);
    }

    public String getDescription() throws ParseException {
        return fetchIfNeeded().getString(KEY_DESCRIPTION);
    }

    public void setParseDescription(String newDescription) {
        put(KEY_DESCRIPTION, newDescription);
    }

    public String getGarmentType() {
        return getString(KEY_GARMENT_TYPE);
    }

    public void setParseGarmentType(String newType) {
        put(KEY_GARMENT_TYPE, newType);
    }

    public String getSubtype() {
        return getString(KEY_SUBTYPE);
    }

    public void setParseSubtype(String newType) {
        put(KEY_SUBTYPE, newType);
    }

    public ParseFile getGarmentPicture() {
        return getParseFile(KEY_GARMENT_PICTURE);
    }

    public void setParseGarmentPicture(ParseFile newPicture) {
        put(KEY_GARMENT_PICTURE, newPicture);
    }

    public Date getDateLastWorn() {
        return getDate(KEY_DATE_LAST_WORN);
    }

    public void setDateLastWorn(Date date) {
        put(KEY_DATE_LAST_WORN, date);
    }

    @Override
    public int compareTo(Garment o) {
        return 0;
    }
}
