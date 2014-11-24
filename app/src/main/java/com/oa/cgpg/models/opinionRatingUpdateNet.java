package com.oa.cgpg.models;

/**
 * Created by Tomasz on 2014-11-24.
 */
public class opinionRatingUpdateNet {
    private int id;
    private int ratingPlus;
    private int ratingMinus;

    public opinionRatingUpdateNet(int id, int ratingPlus, int ratingMinus) {
        this.id = id;
        this.ratingPlus = ratingPlus;
        this.ratingMinus = ratingMinus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRatingPlus() {
        return ratingPlus;
    }

    public void setRatingPlus(int ratingPlus) {
        this.ratingPlus = ratingPlus;
    }

    public int getRatingMinus() {
        return ratingMinus;
    }

    public void setRatingMinus(int ratingMinus) {
        this.ratingMinus = ratingMinus;
    }
}
