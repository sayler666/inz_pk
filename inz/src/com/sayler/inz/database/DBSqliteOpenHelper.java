package com.sayler.inz.database;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.sayler.inz.database.model.Road;
import com.sayler.inz.database.model.Track;

public class DBSqliteOpenHelper extends OrmLiteSqliteOpenHelper {
	
	private final String TAG = "DBSqliteOpenHelper";
	
	private static final String DATABASE_NAME = "gps_ormed.db";
	private static final int DATABASE_VERSION = 7;
	
	public DBSqliteOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		
	}

	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		
		
		try {
			TableUtils.createTable(connectionSource, Road.class);			
			TableUtils.createTable(connectionSource, Track.class);
			Log.d(TAG ,"on create v: "+DATABASE_VERSION);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		Log.d(TAG, "on update v: "+DATABASE_VERSION);
		try {
			TableUtils.dropTable(connectionSource, Road.class, true);
			TableUtils.dropTable(connectionSource, Track.class, true);			
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		onCreate(db, connectionSource);
	}
 
}
