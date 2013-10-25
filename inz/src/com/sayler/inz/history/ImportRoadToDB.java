package com.sayler.inz.history;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import com.sayler.inz.database.model.Track;

public class ImportRoadToDB {
	private I_ImportRoadToDB _importStrategy;
	private List<Track> _road;
	private Date _date;

	public ImportRoadToDB(I_ImportRoadToDB _importStrategy) {
		this._importStrategy = _importStrategy;
	}

	public void setURL(String url) {
		this._importStrategy.setURL(url);
	}

	public void read() throws FileNotFoundException, IOException {
		this._importStrategy.read();
	}

	public List<Track> getTracks() {

		_road = this._importStrategy.getTracks();

		return _road;
	}

	public Date getDate() throws FileNotFoundException, IOException {

		_date = this._importStrategy.getDate();

		return _date;
	}

}
