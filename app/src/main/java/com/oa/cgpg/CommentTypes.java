package com.oa.cgpg;

/**
 * Created by Izabela on 2014-11-07.
 */
import java.util.ArrayList;
public class CommentTypes {
    private String title;
    private ArrayList<CommentType> types;

    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public ArrayList<CommentType> getTypes(){return types;}
    public void setTypes(ArrayList<CommentType> types){
        this.types = types;
    }
}
