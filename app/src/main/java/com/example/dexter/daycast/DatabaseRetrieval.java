package com.example.dexter.daycast;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseRetrieval extends SQLiteOpenHelper {

    private static String DATABASE_NAME = "hackathon.db";

    // Tables
    private String TABLE_ITEM = "Weather_app";
    private String city_selected ;
    //TABLE ITEM
    private String FIELD_TABLE_ITEM_CITY_ID = "city_id";
    private String FIELD_TABLE_ITEM_CITY_NAME = "city_name";
    private String FIELD_TABLE_ITEM_CITY_TEMP = "city_temp";
    private String FIELD_TABLE_ITEM_HUMIDITY_LEVEL = "humidity_level" ;


    public DatabaseRetrieval(Context context , String city_selected) {
        super(context, DATABASE_NAME, null, 1);
        this.city_selected = city_selected;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table Weather_app " +
                        "(city_id text ,city_name text,city_temp text,humidity_level text)"
        );


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS Weather_app");
        onCreate(db);
    }



    public boolean insertItem(WeatherItem weatherItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean isRecordExists = CheckIsDataAlreadyInDBorNot(TABLE_ITEM, FIELD_TABLE_ITEM_CITY_NAME, weatherItem.getCity_name());
        if (isRecordExists) {
            db.update(TABLE_ITEM, addItemFields(weatherItem), FIELD_TABLE_ITEM_CITY_NAME+ " = ?",
                    new String[]{weatherItem.getCity_name()});
        } else {
            db.insert(TABLE_ITEM, null, addItemFields(weatherItem));
        }
        return true;
    }


    private ContentValues addItemFields(WeatherItem weatherItem) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(FIELD_TABLE_ITEM_CITY_ID, weatherItem.getCity_id());
        contentValues.put(FIELD_TABLE_ITEM_CITY_NAME, weatherItem.getCity_name());
        contentValues.put(FIELD_TABLE_ITEM_CITY_TEMP, weatherItem.getCity_temp());
        contentValues.put(FIELD_TABLE_ITEM_HUMIDITY_LEVEL,weatherItem.getHumiditylevel());
        return contentValues;

    }


    public WeatherItem getItem() {
        WeatherItem weatherItem = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "
                + TABLE_ITEM + " where city_name =  \""+city_selected +"\""  + ";", null);
        if(cursor==null){
            System.out.println("Cursor is null");
        }
        if (cursor != null & cursor.getColumnCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {

                String city_id = cursor.getString(cursor
                        .getColumnIndex(FIELD_TABLE_ITEM_CITY_ID));
                String city_name = cursor.getString(cursor
                        .getColumnIndex(FIELD_TABLE_ITEM_CITY_NAME));
                String city_temp = cursor.getString(cursor
                        .getColumnIndex(FIELD_TABLE_ITEM_CITY_TEMP));
                String city_humidity = cursor.getString(cursor
                        .getColumnIndex(FIELD_TABLE_ITEM_HUMIDITY_LEVEL));
                System.out.println("DATABASE");
              weatherItem = new WeatherItem(city_id,city_name,city_temp,city_humidity);
                return weatherItem;

//                cursor.moveToNext();
            }
            cursor.close();
            return weatherItem;
        }
        return null;

    }


    /* Check specific record value in a table */
    public boolean CheckIsDataAlreadyInDBorNot(String TableName,
                                               String dbfield, String fieldValue) {
        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "Select * from " + TableName + " where " + dbfield
                + " = '" + fieldValue + "'";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            return false;
        }
        return true;
    }
    boolean isTableExists(SQLiteDatabase db, String tableName)
    {
        if (tableName == null || db == null || !db.isOpen())
        {
            return false;
        }
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[] {"table", tableName});
        if (!cursor.moveToFirst())
        {
            return false;
        }
        int count = cursor.getInt(0);
        cursor.close();
        return count > 0;
    }


}

    