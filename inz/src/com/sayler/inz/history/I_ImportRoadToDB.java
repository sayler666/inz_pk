package com.sayler.inz.history;

import com.sayler.inz.database.model.Road;

public interface I_ImportRoadToDB {
	void setURL(String url);
	String getURL();
	void read();
	Road getRoad();
	
	
}
