package com.sayler.inz.gps;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "gps.db";
	private static final int DATABASE_VERSION = 2;

	public Database(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

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
