package com.sayler.inz.data;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

import android.os.Handler;
import android.os.Looper;

import com.j256.ormlite.dao.Dao;
import com.sayler.inz.database.model.Identifiable;

public abstract class DataProvider <T extends Identifiable> {
	protected Dao<T, Long> dao;
	private static final Handler mainThread = new Handler(Looper.getMainLooper());

	public DataProvider() {
		dao = setupDao();
	}

	public Dao<T, Long> getDao() {
		return dao;
	}


	/**
	 * Use DaoHelper.getDao(Class<T> type);
	 * 
	 * @return Dao<T, Long> dao - connection to database used to modify it's data
	 */
	protected abstract Dao<T, Long> setupDao();

	public void notifyDataChanged() {

		mainThread.post(new Runnable() {

			@Override
			public void run() {
				postOnMainThread();
			}
		});
	}

	public abstract void postOnMainThread();




	public void deleteAll() {
		try {
			dao.delete(dao.queryForAll());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void delete(long id) {
		try {
			dao.delete(dao.queryForId(id));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void delete(T data) {
		try {
			dao.delete(data);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void delete(List<T> data) {
		try {
			dao.delete(data);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void saveAll(List<T> results) {
		final List<T> localResults = results;
		try {
			dao.callBatchTasks(new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					for (T data : localResults) {
						save(data);
					}
					return null;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	public void save(T data) {
		try {
			dao.createOrUpdate(data);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<T> getAll() throws SQLException {
		return dao.queryForAll();
	}

	public T get(long id) throws SQLException {
		return dao.queryForId(id);
	}
}
