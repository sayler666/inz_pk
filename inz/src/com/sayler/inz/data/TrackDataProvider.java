package com.sayler.inz.data;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;
import com.sayler.inz.database.DaoHelper;
import com.sayler.inz.database.model.Track;

public class TrackDataProvider extends DataProvider<Track> {

	@Override
	protected Dao<Track, Long> setupDao() {
		try {
			return DaoHelper.getDao(Track.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	

	@Override
	public void postOnMainThread() {
				
	}

}
