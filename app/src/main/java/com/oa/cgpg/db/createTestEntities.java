package com.oa.cgpg.db;

import com.oa.cgpg.models.buildingEntity;
import com.oa.cgpg.models.poiEntity;
import com.oa.cgpg.models.typeEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tomasz on 2014-11-06.
 */
public class createTestEntities {
    private dbOps op;
    public createTestEntities() {
    }

    public createTestEntities(dbOps op) {
        this.op = op;
    }

    public buildingEntity generateBuilding(){
        buildingEntity b = new buildingEntity(1,"PG","sda",1,2,3,4,5,6,7,8,"dsadsa");
        return b;
    }

    public typeEntity generateType(){
        typeEntity t = new typeEntity(1,"ATM");
        return t;
    }

    public List<poiEntity> generatePOIs(buildingEntity building,typeEntity type){
        List<poiEntity> listPoi = new ArrayList<poiEntity>();
        listPoi.add(new poiEntity(1, "bankomat 1", building, "dsa", type, 0,0, "dsadas"));
        listPoi.add(new poiEntity(2, "bankomat 2", building, "dsa", type, 0,0, "dsadas"));
        listPoi.add(new poiEntity(3, "bankomat 3", building, "dsa", type, 0,0, "dsadas"));
        return listPoi;
    }

    public void generateTemplateEntities(){
        buildingEntity b = generateBuilding();
        typeEntity t = generateType();
        op.commitBuilding(b);
        op.commitType(t);
        List<poiEntity> lista = generatePOIs(b,t);
        for(poiEntity poi : lista) op.commitPOI(poi);
    }
}
