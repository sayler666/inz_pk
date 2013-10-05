package com.sayler.inz.database;

import java.sql.SQLException;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;

public class DaoHelper {
	private static DBSqliteOpenHelper DBSqliteOpenHelper = null;
	public static <T extends OrmLiteSqliteOpenHelper> void setOpenHelper(Context context, Class<T> type) {
		if (DBSqliteOpenHelper == null) {
			DBSqliteOpenHelper = (DBSqliteOpenHelper) OpenHelperManager.getHelper(context, type);
			Log.d("DaoHelper","null helper");
		}
			
		
	}
	
	public static <T> Dao<T, Long> getDao(Class<T> type) throws SQLException {
		if (DBSqliteOpenHelper != null) {
			return DBSqliteOpenHelper.getDao(type);
		} else {
			return null;
		}
	}
}
