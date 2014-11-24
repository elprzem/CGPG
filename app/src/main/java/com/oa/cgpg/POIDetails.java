package com.oa.cgpg;

/**
 * Created by Izabela on 2014-10-11.
 */
public class POIDetails {
    private String description;
    private int plusesCount;
    private int minusesCount;
    private String imagePath;

    public void setPlusesCount(int plusesCount) {
        this.plusesCount = plusesCount;
    }

    public int getPlusesCount() {
        return plusesCount;
    }

    public void setMinusesCount(int minusesCount) {
        this.minusesCount = minusesCount;
    }

    public int getMinusesCount() {
        return minusesCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }
}
