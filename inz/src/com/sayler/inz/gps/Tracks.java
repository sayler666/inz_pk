package com.sayler.inz.gps;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Tracks {

	public static final String TABLE_TRACKS = "tracks";
	// columns
	public static final String COLUMN_ID = "tracks_id";
	public static final String COLUMN_LAT = "lat";
	public static final String COLUMN_LNG = "lng";
	public static final String COLUMN_ALT = "alt";
	public static final String COLUMN_SPEED = "speed";
	public static final String COLUMN_TIME = "time";
	// TODO change road_id to roads_id
	public static final String COLUMN_ROAD_ID = "road_id";

	private double lat, lng,alt, speed;
	private long time, road_id;

	public Tracks(double lat, double lng,double alt, double speed, long time, long road_id) {

		this.lat = lat;
		this.lng = lng;
		this.alt = alt;
		this.speed = speed;
		this.time = time;
		this.road_id = road_id;
	}

	public static ContentValues getValues(Tracks track) {

		ContentValues values = new ContentValues();
		values.put(COLUMN_LAT, track.lat);
		values.put(COLUMN_LNG, track.lng);
		values.put(COLUMN_ALT, track.alt);
		values.put(COLUMN_SPEED, track.speed);
		values.put(COLUMN_TIME, track.time);
		values.put(COLUMN_ROAD_ID, track.road_id);
		return values;
	}

	

	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_TRACKS + "(" 
			+ COLUMN_ID + " integer primary key autoincrement, " 
			+ COLUMN_LAT + " real not null, " 
			+ COLUMN_LNG + " real not null, "
			+ COLUMN_ALT + " real not null, "
			+ COLUMN_SPEED + " real not null, " 
			+ COLUMN_TIME + " real not null, " 
			+ COLUMN_ROAD_ID + " integer not null"
			+ ");";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(Tracks.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_TRACKS);
		onCreate(database);
	}

}
