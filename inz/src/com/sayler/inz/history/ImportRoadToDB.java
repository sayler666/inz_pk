package com.sayler.inz.history;

import com.sayler.inz.database.model.Road;

public class ImportRoadToDB {
	private I_ImportRoadToDB _importStrategy;
	private Road _road;
	
	public ImportRoadToDB(I_ImportRoadToDB _importStrategy) {
		
		this._importStrategy = _importStrategy;
	}
	
	public void setURL(String url){
		this._importStrategy.setURL(url);
	}
	
	public Road getRoad(){
		
		this._importStrategy.read();
		_road = this._importStrategy.getRoad();
		
		return _road;
	}
	
	
}
