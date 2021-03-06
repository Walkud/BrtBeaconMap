package cn.brt.beacon.map.ext.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cn.brt.beacon.map.ext.bean.data.BeaconEntry;

/**
 * Created by Walkud on 2018/3/16 0016.
 */

public class DBHandler {
    private static DBHandler instance;

    private SQLiteDatabase db;

    public static DBHandler getInstance() {
        if (instance == null) {
            instance = new DBHandler();
        }
        return instance;
    }

    private DBHandler() {
    }

    public void initDb(String dbDir) {
        if (db == null) {
            db = SQLiteDatabase.openDatabase(dbDir, null, SQLiteDatabase.OPEN_READWRITE);
        }
    }

    public List<BeaconEntry> findBecaonList(int floor) {
        List<BeaconEntry> entrys = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select * from " + DbTableConst.BeaconConst.NAME);
        sql.append(" where floor = " + floor);
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql.toString(), null);
            while (cursor.moveToNext()) {
                BeaconEntry entry = new BeaconEntry();
//                entry.setGeom(cursor.getString(cursor.getColumnIndex("geom")));
                entry.setId(cursor.getInt(cursor.getColumnIndex("id")));
                entry.setFloor(cursor.getInt(cursor.getColumnIndex("floor")));
                entry.setMajor(cursor.getShort(cursor.getColumnIndex("major")));
                entry.setMinor(cursor.getShort(cursor.getColumnIndex("minor")));
                entry.setShop_id(cursor.getString(cursor.getColumnIndex("shop_id")));
                entry.setTag(cursor.getString(cursor.getColumnIndex("tag")));
                entry.setUuid(cursor.getString(cursor.getColumnIndex("uuid")));
                entry.setX(cursor.getDouble(cursor.getColumnIndex("x")));
                entry.setY(cursor.getDouble(cursor.getColumnIndex("y")));
                entrys.add(entry);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return entrys;
    }

    public boolean updateBeaconEntity(BeaconEntry beaconEntry) {
        StringBuilder sql = new StringBuilder();
        sql.append("update " + DbTableConst.BeaconConst.NAME);
        sql.append(" set floor = " + beaconEntry.getFloor());
        sql.append(" , uuid = '" + beaconEntry.getUuid() + "'");
        sql.append(" , major = " + beaconEntry.getMajor());
        sql.append(" , minor = " + beaconEntry.getMinor());
        sql.append(" , x = " + beaconEntry.getX());
        sql.append(" , y = " + beaconEntry.getY());
        sql.append(" where id = " + beaconEntry.getId());
        try {
            db.beginTransaction();
            db.execSQL(sql.toString());
            db.setTransactionSuccessful();
            db.endTransaction();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}
