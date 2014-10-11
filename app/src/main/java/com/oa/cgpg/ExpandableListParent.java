package com.oa.cgpg;

/**
 * Created by Izabela on 2014-10-11.
 */
import java.util.ArrayList;
public class ExpandableListParent {
    private String title;
    private boolean checked;
    private ArrayList<ExpandableListChild> children;

    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public boolean isChecked(){
        return checked;
    }
    public void setChecked(boolean checked){
        this.checked = checked;
    }
    public ArrayList<ExpandableListChild> getChildren(){
        return children;
    }
    public void setChildren(ArrayList<ExpandableListChild> children){
        this.children = children;
    }
}
