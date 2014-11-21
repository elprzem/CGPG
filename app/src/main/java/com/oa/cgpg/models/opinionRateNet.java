package com.oa.cgpg.models;

/**
 * Created by Tomasz on 2014-11-21.
 */
public class opinionRateNet {
    private int userId;
    private int opinionId;
    private int value;

    public opinionRateNet(int userId, int opinionId, int value) {
        this.userId = userId;
        this.opinionId = opinionId;
        this.value = value;
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
}
