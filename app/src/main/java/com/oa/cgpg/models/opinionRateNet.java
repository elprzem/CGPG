package com.oa.cgpg.models;

/**
 * Created by Tomasz on 2014-11-21.
 */
public class opinionRateNet {
    private int userId;
    private int opinionId;
    private int value;
    private boolean isUpdated;

    public opinionRateNet(int userId, int opinionId, int value, boolean isUpdated) {
        this.userId = userId;
        this.opinionId = opinionId;
        this.value = value;
        this.isUpdated = isUpdated;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getOpinionId() {
        return opinionId;
    }

    public void setOpinionId(int opinionId) {
        this.opinionId = opinionId;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String isUpdated() {
        return isUpdated?"true":"false";
    }

    public void setUpdated(boolean isUpdated) {
        this.isUpdated = isUpdated;
    }
}
