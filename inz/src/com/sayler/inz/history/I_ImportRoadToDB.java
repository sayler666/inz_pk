package com.sayler.inz.history;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import com.sayler.inz.database.model.Track;
/**
 * Interface for importing road to DB
 * @author lukasz.chromy@gmail.com
 *
 */
public interface I_ImportRoadToDB {
	void setURL(String url);
	String getURL();
	void read() throws FileNotFoundException, IOException;
	List<Track> getTracks();
	Date getDate();
	Double getDistance();
}
