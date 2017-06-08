package com.nyu.cs9033.eta.controllers;

/**
 * The TripDatabaseHelper.java allows user to store, insert,
 * update trip information in the database. Trip details can be
 * viewed from the database.
 *
 *
 * @author  Ponpoorani Ravichandran
 * @version 1.0
 */


import java.util.ArrayList;
import java.util.List;

import com.nyu.cs9033.eta.models.Trip;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

    public class TripDatabaseHelper extends SQLiteOpenHelper {
    //Variable declaration

    private static final int DATABASE_VERSION = 11;
    private static final String DATABASE_NAME= "trips";

    private static final String TABLE_TRIP = "trip";
    private static final String COLUMN_TRIP_NAME = "trip_name";
    private static final String COLUMN_TRIP_ID = "trip_id"; // convention
    private static final String COLUMN_TRIP_DATE = "trip_date";
    private static final String COLUMN_TRIP_TIME = "trip_time";
    private static final String COLUMN_TRIP_DESTINATION = "trip_destination";
    private static final String COLUMN_TRIP_ADDR = "trip_addr";
    private static final String COLUMN_TRIP_LATITUDE = "trip_lat";
    private static final String COLUMN_TRIP_LONGITUDE = "trip_longi";


    public TripDatabaseHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        //Create database table tripInfo
        db.execSQL("create table tripInfo (trip_id text primary key," +
                "trip_name text," +
                "trip_friends text," +
                "trip_date text," +
                "trip_time text," +
                "trip_destination text," +
                "trip_addr text," +
                "trip_lat text," +
                "trip_longi text)");


    }

   //This method will be invoked if the database version is updated
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if exists
        db.execSQL("DROP TABLE IF EXISTS trips");
        db.execSQL("DROP TABLE IF EXISTS location");
        db.execSQL("DROP TABLE IF EXISTS tripInfo" );

        // create tables again
        onCreate(db);
    }


    //allows user to insert trip information in the database.
    //@{param} trip
    public void insertTripInfo(Trip trip) {
        ContentValues cv = new ContentValues();

        System.out.println(trip.getFriends());
        System.out.println(trip.getDate());
        System.out.println(trip.getTime());

        cv.put("trip_id",trip.getTripid().toString());
        cv.put("trip_friends",trip.getFriends().toString());
        cv.put(COLUMN_TRIP_DATE, trip.getDate().toString());
        cv.put(COLUMN_TRIP_NAME, trip.getName().toString());
        cv.put(COLUMN_TRIP_TIME, trip.getTime().toString());
        cv.put(COLUMN_TRIP_DESTINATION, trip.getDestination().toString());
        cv.put(COLUMN_TRIP_ADDR, trip.getDestAddr().toString());
        cv.put(COLUMN_TRIP_LATITUDE, trip.getDesLat().toString());
        cv.put(COLUMN_TRIP_LONGITUDE, trip.getDestLongi().toString());
        // return id of new trip
        getWritableDatabase().insert("tripInfo", null, cv);
    }
    public List<Trip> getAllTrips() {
        List<Trip> tripList = new ArrayList<Trip>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from tripInfo order by 1 desc", null);
        // loop through all query results
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            //cursor.moveToLast();
            //cursor.moveToFirst();
            Trip trip = new Trip();
            trip.setTripid(cursor.getString(0));
            trip.setName(cursor.getString(1));

            trip.setFriends(cursor.getString(2));
            trip.setDate(cursor.getString(3));
            trip.setTime(cursor.getString(4));
            trip.setDestination(cursor.getString(5));
            tripList.add(trip);
        }
        return tripList;//self added
    }

    // get the details about a particular trip
    // @{param} int (trip number)

    public Trip getTripDetails(String num) {
        Trip trip = new Trip();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from tripInfo where trip_id = "+ num, null);

        if(cursor.getCount() != 0){
            cursor.moveToFirst();
            trip.setTripid(cursor.getString(0));
            trip.setName(cursor.getString(1));

            trip.setFriends(cursor.getString(2));
            trip.setDate(cursor.getString(3));
            trip.setTime(cursor.getString(4));
            trip.setDestination(cursor.getString(5));
        }
        return trip;
    }


}
