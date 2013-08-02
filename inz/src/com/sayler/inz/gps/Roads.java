package com.sayler.inz.gps;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Roads {

	public static final String TABLE_ROADS = "roads";
	// columns
	public static final String COLUMN_ID = "roads_id";
	public static final String COLUMN_DISTANCE = "distance";
	public static final String COLUMN_DURATION = "duration";
	public static final String COLUMN_AVG_SPEED = "avg_speed";
	

	private double distance, duration,avg_speed;
	
	/**
	 * Create a new Roads object
	 * @param distance in m
	 * @param duration in seconds
	 * @param avg_speed in KM/h
	 */
	public Roads(double distance, double duration,double avg_speed) {

		this.distance = distance;
		this.duration = duration;
		this.avg_speed = avg_speed;
		
	}

	public static ContentValues getValues(Roads road) {

		ContentValues values = new ContentValues();
		values.put(COLUMN_DISTANCE, road.distance);
		values.put(COLUMN_DURATION, road.duration);
		values.put(COLUMN_AVG_SPEED, road.avg_speed);
	
		return values;
	}

	

	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_ROADS + "(" 
			+ COLUMN_ID + " integer primary key, " 
			+ COLUMN_DISTANCE + " real not null, " 
			+ COLUMN_DURATION + " real not null, "
			+ COLUMN_AVG_SPEED + " real not null "
			+ ");";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(Roads.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_ROADS);
		onCreate(database);
	}

}
