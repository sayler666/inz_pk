package com.sayler.inz.history;

import java.util.List;

import com.sayler.inz.database.model.Track;

public interface I_ImportRoadToDB {
	void setURL(String url);
	String getURL();
	void read();
	List<Track> getTracks();
	
	
}
