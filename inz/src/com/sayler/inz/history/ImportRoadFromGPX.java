package com.sayler.inz.history;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;

import com.sayler.inz.database.model.Track;

/**
 * concrete strategy to import GPX file
 * 
 * @author 2
 * 
 */
public class ImportRoadFromGPX implements I_ImportRoadToDB {

	private final String TAG = "ImportRoadFromGPX";
	private String _url;
	private StringBuilder _totalLine;

	private List<Track> _tracks;

	public ImportRoadFromGPX() {
		super();
	}

	public ImportRoadFromGPX(String url) {
		super();
		this.setURL(url);
	}

	@Override
	public void setURL(String url) {
		this._url = url;
	}

	@Override
	public String getURL() {
		return this._url;
	}

	@Override
	public void read() throws FileNotFoundException, IOException {
		BufferedReader r = null;

		r = new BufferedReader(new InputStreamReader(new FileInputStream(_url)));

		_totalLine = new StringBuilder();
		String line;

		// read file line by line
		while ((line = r.readLine()) != null) {
			_totalLine.append(line);
		}

		r.close();
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	public List<Track> getTracks() {

		_tracks = new ArrayList<Track>();

		// pattern for WTPs
		Pattern patternWpt = Pattern
				.compile("<wpt lat=\"(.*?)\" lon=\"(.*?)\">(.*?)</wpt>");
		// matcher for WTPs
		Matcher matcherWpt = patternWpt.matcher(_totalLine);

		// find all occurrence
		while (matcherWpt.find()) {

			String wptDataString = matcherWpt.group(3);
			// pattern for WTP's alt
			Pattern patternWptAltitude = Pattern.compile("<ele>(.*?)</ele>");
			// matcher for WTP's alt
			Matcher matcherWptAlt = patternWptAltitude.matcher(wptDataString);

			// pattern for WTP's time
			Pattern patternWptTime = Pattern.compile("<time>(.*?)</time>");
			// matcher for WTP's time
			Matcher matcherWptTime = patternWptTime.matcher(wptDataString);

			// Altitude
			Double alt = 0.0;
			if (matcherWptAlt.find()) {
				alt = Double.valueOf(matcherWptAlt.group(1));
			}

			// time
			Date date = null;
			if (matcherWptTime.find()) {
				try {
					date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
							.parse(matcherWptTime.group(1));
				} catch (ParseException e) {
					date = new Date();
					e.printStackTrace();
				}
			}

			Track track = new Track(Double.valueOf(matcherWpt.group(1)),
					Double.valueOf(matcherWpt.group(2)), alt, 0, 0);
			track.setCreatedAt(date);

			// add track to list
			_tracks.add(track);

		}

		return _tracks;

	}

	@SuppressLint("SimpleDateFormat")
	@Override
	public Date getDate() {

		// pattern for time
		Pattern patternTime = Pattern.compile("<time>(.*?)</time>");
		// matcher for time
		Matcher matcherTime = patternTime.matcher(_totalLine);
		// find first occurrence
		Date date = new Date();

		if (matcherTime.find()) {
			try {
				date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
						.parse(matcherTime.group(1));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		return date;
	}

}
