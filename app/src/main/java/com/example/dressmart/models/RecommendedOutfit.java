package com.example.dressmart.models;

import com.example.dressmart.models.parse.Garment;

public class RecommendedOutfit {

    Garment top;
    Garment bottoms;
    Garment outer;
    Garment shoes;

    public RecommendedOutfit(Garment top, Garment bottoms, Garment outer, Garment shoes) {
        this.top = top;
        this.bottoms = bottoms;
        this.outer = outer;
        this.shoes = shoes;
    }

    public Garment getTop() {
        return top;
    }


    public Garment getBottoms() {
        return bottoms;
    }


    public Garment getOuter() {
        return outer;
    }


    public Garment getShoes() {
        return shoes;
    }

}
