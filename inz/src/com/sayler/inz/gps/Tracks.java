package com.sayler.inz.gps;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Tracks {

	public static final String TABLE_TRACKS = "tracks";
	// columns
	public static final String COLUMN_ID = "tracks_id";
	public static final String COLUMN_LANG = "lang";
	public static final String COLUMN_LONG = "long";
	public static final String COLUMN_SPEED = "speed";
	public static final String COLUMN_TIME = "time";
	public static final String COLUMN_ROAD = "road";

	private double lang, lng, speed;
	private long time, road;

	public Tracks(double lang, double lng, double speed, long time, long road) {

		this.lang = lang;
		this.lng = lng;
		this.speed = speed;
		this.time = time;
		this.road = road;
	}

	public static ContentValues getValues(Tracks track) {

		ContentValues values = new ContentValues();
		values.put(COLUMN_LANG, track.lang);
		values.put(COLUMN_LONG, track.lng);
		values.put(COLUMN_SPEED, track.speed);
		values.put(COLUMN_TIME, track.time);
		values.put(COLUMN_ROAD, track.road);
		return values;
	}

	

	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_TRACKS + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_LANG
			+ " real not null, " + COLUMN_LONG + " real not null,"
			+ COLUMN_SPEED + " real not null, " + COLUMN_TIME + " real not null, " + COLUMN_ROAD + " integer not null"
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
