package com.creatvt.ismail.mapapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "maps_creatvt.sqlite";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "tracker";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String ADDRESS = "address";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String TIME = "time";
    private static final String DEFAULT = "DEFAULT";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE " + TABLE_NAME
                + "("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NAME + " TEXT, "
                + ADDRESS + " TEXT, "
                + LATITUDE + " REAL, "
                + LONGITUDE + " REAL, "
                + TIME + " DATETIME DEFAULT CURRENT_TIMESTAMP)";

        db.execSQL(sql);

    }

    public boolean addData(String name,String address,double latitude,double longitude){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(NAME,name);
        values.put(ADDRESS,address);
        values.put(LATITUDE,latitude);
        values.put(LONGITUDE,longitude);
        values.put(TIME,DEFAULT);

        long no = db.insert(TABLE_NAME,null,values);

        db.close();

        return no > 0;

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
