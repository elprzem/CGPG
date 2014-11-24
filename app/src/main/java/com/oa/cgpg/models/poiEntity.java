package com.oa.cgpg.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Tomasz on 2014-11-02.
 */
@DatabaseTable(tableName = "poiDB")
public class poiEntity {
    @DatabaseField(canBeNull = false, id = true)
    private int idPoi;

    @DatabaseField(canBeNull = false)
    private String name;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "building_idBuilding", canBeNull = false)
    private buildingEntity building;

    @DatabaseField(canBeNull = false)
    private String description;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "type_idType", canBeNull = false)
    private typeEntity type;

    @DatabaseField(canBeNull = false)
    private int ratingPlus;

    @DatabaseField(canBeNull = false)
    private int ratingMinus;

    @DatabaseField(canBeNull = false)
    private String linkToImage;

    public poiEntity() {
    }

    public poiEntity(int idPoi, String name, buildingEntity building, String description, typeEntity type, int ratingPlus, int ratingMinus, String linkToImage) {
        this.idPoi = idPoi;
        this.name = name;
        this.building = building;
        this.description = description;
        this.type = type;
        this.ratingPlus = ratingPlus;
        this.ratingMinus = ratingMinus;
        this.linkToImage = linkToImage;
    }

    public int getIdPoi() {
        return idPoi;
    }

    public void setIdPoi(int idPoi) {
        this.idPoi = idPoi;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public buildingEntity getBuilding() {
        return building;
    }

    public void setBuilding(buildingEntity building) {
        this.building = building;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public typeEntity getType() {
        return type;
    }

    public void setType(typeEntity type) {
        this.type = type;
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

    public String getLinkToImage() {
        return linkToImage;
    }

    public void setLinkToImage(String linkToImage) {
        this.linkToImage = linkToImage;
    }

    @Override
    public String toString() {
        return "poiEntity{" +
                "idPoi=" + idPoi +
                ", name='" + name + '\'' +
                ", building=" + building +
                ", description='" + description + '\'' +
                ", type=" + type +
                ", ratingPlus=" + ratingPlus +
                ", ratingMinus=" + ratingMinus +
                ", linkToImage='" + linkToImage + '\'' +
                '}';
    }
}
