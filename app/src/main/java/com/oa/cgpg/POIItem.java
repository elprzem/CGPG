package com.oa.cgpg;

/**
 * Created by Izabela on 2014-10-11.
 */

import java.util.ArrayList;

public class POIItem {
    private String title;
    private int id;
    private boolean checked;
    private ArrayList<POIDetails> details;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public ArrayList<POIDetails> getDetails() {
        return details;
    }

    public void setDetails(ArrayList<POIDetails> details) {
        this.details = details;
    }
}
