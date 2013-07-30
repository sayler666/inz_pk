package com.sayler.inz.gps;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "gps.db";
	private static final int DATABASE_VERSION = 3;

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

	// SELECTS
	//
	// Track table

	public int getNexRoadId() {

		SQLiteDatabase database = this.getReadableDatabase();

		Cursor roadIdCursor = database.rawQuery("SELECT max("
				+ Tracks.COLUMN_ROAD + ")+1 FROM " + Tracks.TABLE_TRACKS, null);
		roadIdCursor.moveToFirst();
		if(roadIdCursor.getInt(0) == 0){
			return 1;
		}else{
			return roadIdCursor.getInt(0);
		}
		
		
	}

	// Create all tables
	@Override
	public void onCreate(SQLiteDatabase database) {

		Tracks.onCreate(database);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Tracks.onUpgrade(database, oldVersion, newVersion);

	}

}
