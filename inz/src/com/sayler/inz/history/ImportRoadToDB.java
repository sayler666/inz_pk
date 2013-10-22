package com.sayler.inz.history;

import java.util.List;

import com.sayler.inz.database.model.Track;

public class ImportRoadToDB {
	private I_ImportRoadToDB _importStrategy;
	private List<Track> _road;
	
	public ImportRoadToDB(I_ImportRoadToDB _importStrategy) {
		
		this._importStrategy = _importStrategy;
	}
	
	public void setURL(String url){
		this._importStrategy.setURL(url);
	}
	
	public List<Track> getTracks(){
		
		this._importStrategy.read();
		_road = this._importStrategy.getTracks();
		
		return _road;
	}
	
	
}
