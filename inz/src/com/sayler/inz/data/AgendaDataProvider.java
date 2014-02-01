package com.sayler.inz.data;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;
import com.sayler.inz.database.DaoHelper;
import com.sayler.inz.database.model.Agenda;
import com.sayler.inz.database.model.Track;

public class AgendaDataProvider extends DataProvider<Agenda> {

	@Override
	protected Dao<Agenda, Long> setupDao() {
		try {
			return DaoHelper.getDao(Agenda.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	

	@Override
	public void postOnMainThread() {
				
	}

}
