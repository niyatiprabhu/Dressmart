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

    public void setTop(Garment top) {
        this.top = top;
    }

    public Garment getBottoms() {
        return bottoms;
    }

    public void setBottoms(Garment bottoms) {
        this.bottoms = bottoms;
    }

    public Garment getOuter() {
        return outer;
    }

    public void setOuter(Garment outer) {
        this.outer = outer;
    }

    public Garment getShoes() {
        return shoes;
    }

    public void setShoes(Garment shoes) {
        this.shoes = shoes;
    }
}
