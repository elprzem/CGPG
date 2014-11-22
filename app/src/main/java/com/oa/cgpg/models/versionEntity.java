package com.oa.cgpg.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Tomasz on 2014-11-02.
 */
@DatabaseTable(tableName = "versionDB")
public class versionEntity {
    @DatabaseField(canBeNull = false,id=true)
    private int id;

    @DatabaseField(canBeNull = false)
    private int versionNumber;

    public versionEntity() {
    }

    public versionEntity(int id,int i) {
        this.id = id;
        this.versionNumber = i;
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(int versionNumber) {
        this.versionNumber = versionNumber;
    }

    @Override
    public String toString() {
        return "versionEntity{" +
                "id=" + id +
                ", versionNumber=" + versionNumber +
                '}';
    }
}
