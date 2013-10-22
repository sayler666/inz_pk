package com.sayler.inz.history;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

import com.sayler.inz.database.model.Track;

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
	public void read() {
		BufferedReader r = null;
		try {
			r = new BufferedReader(new InputStreamReader(new FileInputStream(
					_url)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		_totalLine = new StringBuilder();
		String line;
		try {
			// read file line by line
			while ((line = r.readLine()) != null) {
				_totalLine.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Track> getTracks() {

		_tracks = new ArrayList<Track>();

		// pattern for time
		Pattern patternTime = Pattern.compile("<time>(.*?)</time>");
		// matcher for time
		Matcher matcherTime = patternTime.matcher(_totalLine);
		// find first occurrence
		matcherTime.find();
		Log.d(TAG, matcherTime.group(1));

		// pattern for WTPs
		Pattern patternWpt = Pattern
				.compile("<wpt lat=\"(.*?)\" lon=\"(.*?)\">(.*?)</wpt>");
		// matcher for WTPs
		Matcher matcherWpt = patternWpt.matcher(_totalLine);

		// find all occurrence
		while (matcherWpt.find()) {
			_tracks.add(new Track(Double.valueOf(matcherWpt.group(1)), Double
					.valueOf(matcherWpt.group(2)), 0, 0, 0));
		}

		return _tracks;

	}

}
