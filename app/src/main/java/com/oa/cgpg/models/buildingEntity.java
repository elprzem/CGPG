package com.oa.cgpg.models;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Tomasz on 2014-11-02.
 */

@DatabaseTable(tableName = "buildingDB")
public class buildingEntity {
    @DatabaseField(canBeNull = false, id = true)
    private int idBuilding;

    @DatabaseField(canBeNull = false)
    private String name;

    @DatabaseField(canBeNull = false)
    private String description;

    @DatabaseField(canBeNull = false)
    private int x1;

    @DatabaseField(canBeNull = false)
    private int y1;

    @DatabaseField(canBeNull = false)
    private int x2;

    @DatabaseField(canBeNull = false)
    private int y2;

    @DatabaseField(canBeNull = false)
    private int x3;

    @DatabaseField(canBeNull = false)
    private int y3;

    @DatabaseField(canBeNull = false)
    private int x4;

    @DatabaseField(canBeNull = false)
    private int y4;

    @DatabaseField(canBeNull = false)
    private String linkToImage;

    private ForeignCollection<poiEntity> pois;

    public buildingEntity() {

    }

    public buildingEntity(int idBuilding, String name, String description, int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4, String linkToImage) {
        this.idBuilding = idBuilding;
        this.name = name;
        this.description = description;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.x3 = x3;
        this.y3 = y3;
        this.x4 = x4;
        this.y4 = y4;
        this.linkToImage = linkToImage;
    }

    public ForeignCollection<poiEntity> getPois() {
        return pois;
    }

    public int getIdBuilding() {
        return idBuilding;
    }

    public void setIdBuilding(int idBuilding) {
        this.idBuilding = idBuilding;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getX1() {
        return x1;
    }

    public void setX1(int x1) {
        this.x1 = x1;
    }

    public int getY1() {
        return y1;
    }

    public void setY1(int y1) {
        this.y1 = y1;
    }

    public int getX2() {
        return x2;
    }

    public void setX2(int x2) {
        this.x2 = x2;
    }

    public int getY2() {
        return y2;
    }

    public void setY2(int y2) {
        this.y2 = y2;
    }

    public int getX3() {
        return x3;
    }

    public void setX3(int x3) {
        this.x3 = x3;
    }

    public int getY3() {
        return y3;
    }

    public void setY3(int y3) {
        this.y3 = y3;
    }

    public int getX4() {
        return x4;
    }

    public void setX4(int x4) {
        this.x4 = x4;
    }

    public int getY4() {
        return y4;
    }

    public void setY4(int y4) {
        this.y4 = y4;
    }

    public String getLinkToImage() {
        return linkToImage;
    }

    public void setLinkToImage(String linkToImage) {
        this.linkToImage = linkToImage;
    }

    @Override
    public String toString() {
        return "buildingEntity{" +
                "idBuilding=" + idBuilding +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", x1=" + x1 +
                ", y1=" + y1 +
                ", x2=" + x2 +
                ", y2=" + y2 +
                ", x3=" + x3 +
                ", y3=" + y3 +
                ", x4=" + x4 +
                ", y4=" + y4 +
                ", linkToImage='" + linkToImage + '\'' +
                ", pois=" + pois +
                '}';
    }
}
