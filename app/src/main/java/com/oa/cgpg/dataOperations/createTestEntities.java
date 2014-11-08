package com.oa.cgpg.dataOperations;

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

    public buildingEntity generateBuilding(int id, String name, String desc){
        buildingEntity b = new buildingEntity(id,name,desc,1,2,3,4,5,6,7,8,"dsadsa");
        return b;
    }

    public typeEntity generateType(int id, String name){
        typeEntity t = new typeEntity(id,name);
        return t;
    }

    public List<poiEntity> generatePOIs(buildingEntity building,typeEntity type, int id_first){
        List<poiEntity> listPoi = new ArrayList<poiEntity>();
        listPoi.add(new poiEntity(id_first, "punkt 1 typu: "+type.getName(), building, "dsa", type, 0,0, "dsadas"));
        listPoi.add(new poiEntity(id_first+1, "punkt 2 typu: "+type.getName(), building, "dsa", type, 0,0, "dsadas"));
        listPoi.add(new poiEntity(id_first+2, "punkt 3 typu:"+type.getName(), building, "dsa", type, 0,0, "dsadas"));
        return listPoi;
    }

    public void generateTemplateEntities(){
        buildingEntity b1 = generateBuilding(1,"główny", "la la la");
        op.commitBuilding(b1);
        typeEntity t1 = generateType(1,"XERO");
        op.commitType(t1);
        typeEntity t2 = generateType(2,"BUFFET");
        op.commitType(t2);
        typeEntity t3 = generateType(3,"AUTOMAT");
        op.commitType(t3);
        typeEntity t4 = generateType(4,"READROOM");
        op.commitType(t4);
        typeEntity t5 = generateType(5,"ATM");
        op.commitType(t5);
        typeEntity t6 = generateType(6,"RELAX");
        op.commitType(t6);
        typeEntity t7 = generateType(7,"BIKES");
        op.commitType(t7);
        List<poiEntity> lista1 = generatePOIs(b1,t1,1);
        for(poiEntity poi : lista1) op.commitPOI(poi);
        List<poiEntity> lista2 = generatePOIs(b1,t2,4);
        for(poiEntity poi : lista2) op.commitPOI(poi);
        List<poiEntity> lista3 = generatePOIs(b1,t3,7);
        for(poiEntity poi : lista3) op.commitPOI(poi);
        List<poiEntity> lista4 = generatePOIs(b1,t4,10);
        for(poiEntity poi : lista4) op.commitPOI(poi);
        List<poiEntity> lista5 = generatePOIs(b1,t5,13);
        for(poiEntity poi : lista5) op.commitPOI(poi);
        List<poiEntity> lista6 = generatePOIs(b1,t6,16);
        for(poiEntity poi : lista6) op.commitPOI(poi);
        List<poiEntity> lista7 = generatePOIs(b1,t7,19);
        for(poiEntity poi : lista7) op.commitPOI(poi);
    }
}
