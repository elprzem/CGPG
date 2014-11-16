package com.oa.cgpg.dataOperations;

import android.util.Log;
import com.j256.ormlite.android.apptools.OrmLiteBaseListActivity;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.stmt.QueryBuilder;
import com.oa.cgpg.models.buildingEntity;
import com.oa.cgpg.models.poiEntity;
import com.oa.cgpg.models.typeEntity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tomasz on 2014-11-05.
 */
public class dbOps extends OrmLiteBaseListActivity<dataBaseHelper> {
    private Dao<buildingEntity, Integer> buildingDAO;
    private Dao<typeEntity, Integer> typeDAO;
    private Dao<poiEntity, Integer> poiDAO;
    //dataBaseHelper databaseHelper = null;

    public dbOps(final dataBaseHelper databaseHelper) {
        this.buildingDAO = getBuildingDAO(databaseHelper);
        this.typeDAO = getTypeDAO(databaseHelper);
        this.poiDAO = getPoiDAO(databaseHelper);
    }
   /* public dbOps() {
        databaseHelper = getHelper();
        this.buildingDAO = getBuildingDAO(databaseHelper);
        this.typeDAO = getTypeDAO(databaseHelper);
        this.poiDAO = getPoiDAO(databaseHelper);
    }*/

    private Dao<poiEntity, Integer> getPoiDAO(final dataBaseHelper databaseHelper) {
        if (null == this.poiDAO) {
            try {
                this.poiDAO = databaseHelper.getPoiDAO();
            } catch (final SQLException e) {
                Log.e("DbOPS", "Unable to load DAO: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return this.poiDAO;
    }

    private Dao<typeEntity, Integer> getTypeDAO(final dataBaseHelper databaseHelper) {
        if (null == this.typeDAO) {
            try {
                this.typeDAO = databaseHelper.getTypeDAO();
            } catch (final SQLException e) {
                Log.e("DbOPS", "Unable to load DAO: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return this.typeDAO;
    }

    private Dao<buildingEntity, Integer> getBuildingDAO(final dataBaseHelper databaseHelper) {
        if (null == this.buildingDAO) {
            try {
                this.buildingDAO = databaseHelper.getBuildingDAO();
            } catch (final SQLException e) {
                Log.e("DbOPS", "Unable to load DAO: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return this.buildingDAO;
    }

    public List<buildingEntity> getBuildings() {
        try {
            return this.buildingDAO.queryForAll();
        } catch (final SQLException e) {
            Log.e("DbOPS", "Unable to load DAO: " + e.getMessage());
        }
        return new ArrayList<buildingEntity>();
    }

    public List<typeEntity> getTypes() {
        try {
            return this.typeDAO.queryForAll();
        } catch (final SQLException e) {
            Log.e("DbOPS", "Unable to load DAO: " + e.getMessage());
        }
        return new ArrayList<typeEntity>();
    }

    public List<poiEntity> getPois() {
        try {
            return this.poiDAO.queryForAll();
        } catch (final SQLException e) {
            Log.e("DbOPS", "Unable to load DAO: " + e.getMessage());

        }
        return new ArrayList<poiEntity>();
    }

    public buildingEntity getBuildingById(int id){
        buildingEntity build = null;
            try { QueryBuilder<buildingEntity,Integer> getById = buildingDAO.queryBuilder();

            getById.where().eq("idBuilding",id);
            List<buildingEntity> list = getById.query();
            build = list.get(0);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return build;
    }

    public void clearData() {
        final List<poiEntity> pois = getPois();
        if (!pois.isEmpty())
            for (final poiEntity poi : pois) {
                deletePoi(poi);
            }
        final List<buildingEntity> buildings = getBuildings();
        if (!buildings.isEmpty())
            for (final buildingEntity build : buildings) {
                deleteBuilding(build);
            }
        final List<typeEntity> types = getTypes();
        if (!types.isEmpty())
            for (final typeEntity type : types) {
                deleteType(type);
            }
    }

    public void deletePoi(poiEntity poi) {
        try {
            this.poiDAO.delete(poi);
        } catch (SQLException e) {
            Log.e("DbOPS", "Unable to delete POI: " + e.getMessage());
        }
    }

    public void deleteBuilding(buildingEntity build) {
        try {
            ForeignCollection<poiEntity> pois = build.getPois();
            for (poiEntity poi : pois) deletePoi(poi);
            this.buildingDAO.delete(build);
        } catch (SQLException e) {
            Log.e("DbOPS", "Unable to delete Building: " + e.getMessage());
        }
    }

    public void deleteType(typeEntity type) {
        try {
            ForeignCollection<poiEntity> pois = type.getPois();
            for (poiEntity poi : pois) deletePoi(poi);
            this.typeDAO.delete(type);
        } catch (SQLException e) {
            Log.e("DbOPS", "Unable to delete Type: " + e.getMessage());
        }
    }

    public void commitPOI(poiEntity poi) {
        try {
            this.poiDAO.createOrUpdate(poi);
        } catch (SQLException e) {
            Log.e("DbOPS", "Unable to commit POI: " + e.getMessage());
        }
    }

    public void commitType(typeEntity type) {
        try {
            this.typeDAO.createOrUpdate(type);
        } catch (SQLException e) {
            Log.e("DbOPS", "Unable to commit Type: " + e.getMessage());
        }
    }

    public void commitBuilding(buildingEntity build) {
        try {
            this.buildingDAO.createOrUpdate(build);
        } catch (SQLException e) {
            Log.e("DbOPS", "Unable to commit Building: " + e.getMessage());
        }
    }
}
