package com.oa.cgpg.dataOperations;

import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteBaseListActivity;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.table.TableUtils;
import com.oa.cgpg.models.buildingEntity;
import com.oa.cgpg.models.poiEntity;
import com.oa.cgpg.models.typeEntity;
import com.oa.cgpg.models.versionEntity;

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
    private Dao<versionEntity, Integer> versionDAO;
    private dataBaseHelper dbHelper = null;

    public dbOps(final dataBaseHelper databaseHelper) {
        this.buildingDAO = getBuildingDAO(databaseHelper);
        this.typeDAO = getTypeDAO(databaseHelper);
        this.poiDAO = getPoiDAO(databaseHelper);
        this.versionDAO = getVersionDAO(databaseHelper);
        this.dbHelper = databaseHelper;
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

    private Dao<versionEntity, Integer> getVersionDAO(final dataBaseHelper databaseHelper) {
        if (null == this.versionDAO) {
            try {
                this.versionDAO = databaseHelper.getVersionDAO();
            } catch (final SQLException e) {
                Log.e("DbOPS", "Unable to load DAO: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return this.versionDAO;
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

    public buildingEntity getBuildingById(int id) {
        buildingEntity build = null;
        try {
            QueryBuilder<buildingEntity, Integer> getById = buildingDAO.queryBuilder();
            getById.where().eq("idBuilding", id);
            List<buildingEntity> list = getById.query();
            build = list.get(0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return build;
    }
    public typeEntity getTypeById(int id) {
        typeEntity type = null;
        try {
            QueryBuilder<typeEntity, Integer> getById = typeDAO.queryBuilder();
            getById.where().eq("idType", id);
            List<typeEntity> list = getById.query();
            type = list.get(0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return type;
    }

    public versionEntity getVersion() {
        versionEntity version = null;

        try {
            QueryBuilder<versionEntity, Integer> getVer = versionDAO.queryBuilder();
            List<versionEntity> list = getVer.query();
            version = list.get(0);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return version;
    }

    // TODO
    public List<buildingEntity> getBuildingsCoordinatesByTypePOI() {
        List<buildingEntity> list = null;
        QueryBuilder<buildingEntity, Integer> getById = buildingDAO.queryBuilder();

        return list;
    }

    //przepisuje Przemka warunki
    //metoda zwraca ID lub -1 w przypadku braku budynku
    public int getIdOfBuildingByCords(int x, int y) throws Exception {
        QueryBuilder<buildingEntity, Integer> getByCords = buildingDAO.queryBuilder();
        Where where = getByCords.where();
        where.lt("x1", x);
        where.and();
        where.lt("x4", x);
        where.and();
        where.gt("x2", x);
        where.and();
        where.gt("x3", x);
        where.and();
        where.gt("y1", y);
        where.and();
        where.gt("y2", y);
        where.and();
        where.lt("y3", y);
        where.and();
        where.lt("y4", y);
        PreparedQuery<buildingEntity> request = getByCords.prepare();
        List<buildingEntity> list = buildingDAO.query(request);
        if (list.size() != 0) {
            return list.get(0).getIdBuilding();
        } else {
            return -1;
        }
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

    public void changeVersion(int vNumber) {
        versionEntity ver = getVersion();
        ver.setVersionNumber(vNumber);
        try {
            this.versionDAO.createOrUpdate(ver);
        } catch (SQLException e) {
            Log.e("DbOPS", "Unable to commit Version: " + e.getMessage());
        }
    }

    public void update() {
        try {
            TableUtils.dropTable(dbHelper.getConnectionSource(), poiEntity.class, true);
            TableUtils.dropTable(dbHelper.getConnectionSource(), typeEntity.class, true);
            TableUtils.dropTable(dbHelper.getConnectionSource(), buildingEntity.class, true);

            TableUtils.createTable(dbHelper.getConnectionSource(), buildingEntity.class);
            TableUtils.createTable(dbHelper.getConnectionSource(), typeEntity.class);
            TableUtils.createTable(dbHelper.getConnectionSource(), poiEntity.class);

            changeVersion(213);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
