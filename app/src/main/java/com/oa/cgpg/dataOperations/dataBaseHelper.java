package com.oa.cgpg.dataOperations;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.oa.cgpg.models.buildingEntity;
import com.oa.cgpg.models.poiEntity;
import com.oa.cgpg.models.typeEntity;

import java.sql.SQLException;

/**
 * Created by Tomasz on 2014-11-05.
 */
public class dataBaseHelper extends OrmLiteSqliteOpenHelper {
    private XMLParsing parser;
    private static final String DATABASE_NAME = "cgpg.db";
    // any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = 3;

    public dataBaseHelper(final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private Dao<buildingEntity, Integer> buildingDAO = null;
    private Dao<typeEntity, Integer> typeDAO = null;
    private Dao<poiEntity, Integer> poiDAO = null;

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, final ConnectionSource connectionSource) {
        Log.i(dataBaseHelper.class.getName(), "onCreate");
        try {
            TableUtils.createTable(connectionSource, buildingEntity.class);
            TableUtils.createTable(connectionSource, typeEntity.class);
            TableUtils.createTable(connectionSource, poiEntity.class);
        } catch (SQLException e) {
            Log.e(dataBaseHelper.class.getName(), "Can't create database", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, final ConnectionSource connectionSource, int i, int i2) {
        Log.i(dataBaseHelper.class.getName(), "onUpgrade");
        try {
            TableUtils.dropTable(connectionSource, poiEntity.class, true);
            TableUtils.dropTable(connectionSource, typeEntity.class, true);
            TableUtils.dropTable(connectionSource, buildingEntity.class, true);
            Log.i(dataBaseHelper.class.getName(), "DB dropped");
            onCreate(sqLiteDatabase,connectionSource);
        } catch (SQLException e) {
            Log.e(dataBaseHelper.class.getName(), "Can't drop database", e);
        }

    }

    public Dao<buildingEntity, Integer> getBuildingDAO() throws SQLException {
        if (this.buildingDAO == null) {
            this.buildingDAO = getDao(buildingEntity.class);
        }
        return this.buildingDAO;
    }

    public Dao<typeEntity, Integer> getTypeDAO() throws SQLException {
        if (this.typeDAO == null) {
            this.typeDAO = getDao(typeEntity.class);
        }
        return this.typeDAO;
    }

    public Dao<poiEntity, Integer> getPoiDAO() throws SQLException {
        if (this.poiDAO == null) {
            this.poiDAO = getDao(poiEntity.class);
        }
        return this.poiDAO;
    }

    @Override
    public void close() {
        super.close();
        this.poiDAO = null;
        this.typeDAO = null;
        this.buildingDAO = null;
    }
}
