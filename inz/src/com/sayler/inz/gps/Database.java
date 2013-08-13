package com.sayler.inz.gps;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Database extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "gps.db";
	private static final int DATABASE_VERSION = 7;
	private static final String TAG = "Database";

	public Database(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * Add Track to database
	 * 
	 * @param track
	 * @return id of added track
	 */
	public long addTrack(Tracks track) {

		SQLiteDatabase database = this.getWritableDatabase();
		database.beginTransaction();
		long id = 0;

		try {
			id = database.insert(Tracks.TABLE_TRACKS, null,
					Tracks.getValues(track));
			database.setTransactionSuccessful();
		} catch (Exception e) {
			database.endTransaction();
		} finally {
			database.endTransaction();
		}
		return id;
	}

	/**
	 * Add Roads to database
	 * 
	 * @param roads
	 * @return id of added roads
	 */
	public long addRoad(Roads road) {

		SQLiteDatabase database = this.getWritableDatabase();
		database.beginTransaction();
		long id = 0;

		try {
			id = database
					.insert(Roads.TABLE_ROADS, null, Roads.getValues(road));
			database.setTransactionSuccessful();
		} catch (Exception e) {
			database.endTransaction();
		} finally {
			database.endTransaction();
		}
		return id;
	}

	// SELECTS
	//
	// Track table

	/**
	 * next road id
	 * 
	 * @return
	 */
	public int getNexRoadId() {

		SQLiteDatabase database = this.getReadableDatabase();

		Cursor roadIdCursor = database.rawQuery("SELECT max(" + Roads.COLUMN_ID
				+ ")+1 FROM " + Roads.TABLE_ROADS, null);
		roadIdCursor.moveToFirst();
		if (roadIdCursor.getInt(0) == 0) {
			return 1;
		} else {
			return roadIdCursor.getInt(0);
		}

	}

	/**
	 * Get all roads
	 * 
	 * @return
	 */
	public Cursor getAllRoads() {

		SQLiteDatabase database = this.getReadableDatabase();

		// cursorAdapter need column _id
		String buildSQL = "SELECT rowid _id,* FROM " + Roads.TABLE_ROADS
				+ " ORDER BY " + Roads.COLUMN_ID + " DESC";

		// Log.d(TAG, "getAllRoads SQL: " + buildSQL);

		return database.rawQuery(buildSQL, null);
	}

	public Cursor getRoadById(long roadId) {
		SQLiteDatabase database = this.getReadableDatabase();

		String buildSQL = "SELECT * FROM " + Tracks.TABLE_TRACKS + " JOIN "
				+ Roads.TABLE_ROADS + "  ON " + Roads.TABLE_ROADS + "."
				+ Roads.COLUMN_ID + "  = " + Tracks.TABLE_TRACKS + "."
				+ Tracks.COLUMN_ROAD_ID + " where " + Roads.COLUMN_ID + " = "
				+ roadId;

		Log.d(TAG, "getRoadById SQL: " + buildSQL);
		
		return database.rawQuery(buildSQL, null);
	}

	// Create all tables
	@Override
	public void onCreate(SQLiteDatabase database) {

		Tracks.onCreate(database);

		Roads.onCreate(database);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {

		Tracks.onUpgrade(database, oldVersion, newVersion);

		Roads.onUpgrade(database, oldVersion, newVersion);

	}

}
