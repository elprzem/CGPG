package com.oa.cgpg.models;

import java.util.Date;

/**
 * Created by Tomasz on 2014-11-12.
 */
public class opinionNetEntity {
    private int id;
    private String opinionText;
    private String username;
    private int poiId;
    private int ratingPlus;
    private int ratingMinus;
    private int val;
    private int opinionType;
    private Date addDate;

    public opinionNetEntity() {
    }

    public opinionNetEntity(int id, String opinionText, String username, int poiId, int ratingPlus, int ratingMinus,
                            int val, int opinionType, Date addDate) {
        this.id = id;
        this.opinionText = opinionText;
        this.username = username;
        this.poiId = poiId;
        this.ratingPlus = ratingPlus;
        this.ratingMinus = ratingMinus;
        this.val = val;
        this.opinionType = opinionType;
        this.addDate = addDate;
    }

    @Override
    public String toString () {
        return "opinionNetEntity{" +
                       "id=" + id +
                       ", opinionText='" + opinionText + '\'' +
                       ", username='" + username + '\'' +
                       ", poiId=" + poiId +
                       ", ratingPlus=" + ratingPlus +
                       ", ratingMinus=" + ratingMinus +
                       ", val=" + val +
                       ", opinionType=" + opinionType +
                       ", addDate=" + addDate +
                       '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOpinionText() {
        return opinionText;
    }

    public void setOpinionText(String opinionText) {
        this.opinionText = opinionText;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getPoiId() {
        return poiId;
    }

    public void setPoiId(int poiId) {
        this.poiId = poiId;
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

    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }

    public int getOpinionType () {
        return opinionType;
    }

    public void setOpinionType (int opinionType) {
        this.opinionType = opinionType;
    }
}
