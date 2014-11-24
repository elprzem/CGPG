package com.oa.cgpg;

/**
 * Created by Izabela on 2014-11-07.
 */

import java.util.ArrayList;

public class OpinionTypes {
    private String title;
    private ArrayList<OpinionType> types;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<OpinionType> getTypes() {
        return types;
    }

    public void setTypes(ArrayList<OpinionType> types) {
        this.types = types;
    }
}
