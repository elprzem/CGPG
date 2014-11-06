package com.oa.cgpg.models;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
/**
 * Created by Tomasz on 2014-11-02.
 */
@DatabaseTable(tableName = "typeDB")
public class typeEntity {
    @DatabaseField(generatedId = false, canBeNull = false)
    private int idType;

    @DatabaseField(canBeNull = false)
    private String name;

    @ForeignCollectionField
    private ForeignCollection<poiEntity> pois;

    public typeEntity() {
    }
    public ForeignCollection<poiEntity> getPois() {
        return this.pois;
    }

    public int getIdType() {
        return idType;
    }

    public void setIdType(int idType) {
        this.idType = idType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
