package com.oa.cgpg;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Izabela on 2014-11-13.
 */
public class Opinion {
    private String text;
    private int id;
    private String username;
    private int poiId;
    private int ratingPlus;
    private int ratingMinus;
    private int val;
    private int opinionType;
    private Date addDate;

    public String getText(){
        return text;
    }
    public void setText(String title){
        this.text = text;
    }
    public int getId() { return  id;}
    public void setId(int id){this.id = id;}
    public String getUsername () {return username;}
    public void setUsername (String username){this.username = username;}
    public int getPoiId() {return poiId;}
    public void setPoiId(int poiId){this.poiId = poiId;}
}
