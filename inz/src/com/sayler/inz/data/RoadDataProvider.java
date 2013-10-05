package com.sayler.inz.data;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;
import com.sayler.inz.database.DaoHelper;
import com.sayler.inz.database.model.Road;

public class RoadDataProvider extends DataProvider<Road> {

	@Override
	protected Dao<Road, Long> setupDao() {
		try {
			return DaoHelper.getDao(Road.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	

	@Override
	public void postOnMainThread() {
		
		
	}

}
