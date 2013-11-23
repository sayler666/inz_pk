package com.sayler.inz.history.gpx;

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
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.sayler.inz.database.model.Track;

/**
 * concrete strategy to import GPX file
 * 
 * @author 2
 * 
 */
public class ImportRoadFromGPX implements I_ImportRoadToDB {

	private static final String TAG = "ImportRoadFromGPX";
	private String _url;
	private StringBuilder _totalLine;

	private List<Track> _tracks;
	private Double _distance = 0.0, _duration;

	// patterns
	// pattern for WTPs
	Pattern patternWpt = Pattern
			.compile("<(?:wpt|trkpt) lat=\"(.*?)\" lon=\"(.*?)\">(.*?)</(?:wpt|trkpt)>");
	// pattern for time
	Pattern patternTime = Pattern.compile("<time>(.*?)</time>");
	// pattern for WTP's elevation
	Pattern patternWptAltitude = Pattern.compile("<ele>(.*?)</ele>");

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

		// matcher for WTPs
		Matcher matcherWpt = patternWpt.matcher(_totalLine);

		// find all occurrence
		while (matcherWpt.find()) {

			String wptDataString = matcherWpt.group(3);

			// matcher for WTP's altitude
			Matcher matcherWptAlt = patternWptAltitude.matcher(wptDataString);

			// matcher for WTP's time
			Matcher matcherWptTime = patternTime.matcher(wptDataString);

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
					Double.valueOf(matcherWpt.group(2)), ((alt != null) ? alt
							: 0), 0, ((date != null) ? date.getTime() : 0));
			track.setCreatedAt(date);

			// add track to list
			_tracks.add(track);

		}

		return _tracks;

	}

	@SuppressLint("SimpleDateFormat")
	@Override
	public Date getDate() {

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

	@Override
	public Double getDistance() {

		// matcher for WTPs
		Matcher matcherWpt = patternWpt.matcher(_totalLine);
		// remember last location
		LatLng mLastLocation = null;
		// find all occurrence
		while (matcherWpt.find()) {
			// current location
			LatLng location = new LatLng(Double.valueOf(matcherWpt.group(1)),
					Double.valueOf(matcherWpt.group(2)));

			// calculate distance
			if (mLastLocation != null) {
				float[] results = new float[5];
				Location.distanceBetween(mLastLocation.latitude,
						mLastLocation.longitude, location.latitude,
						location.longitude, results);
				_distance += results[0];
			}

			mLastLocation = new LatLng(Double.valueOf(matcherWpt.group(1)),
					Double.valueOf(matcherWpt.group(2)));
		}

		return _distance;
	}

	@Override
	public Double getDuration() {

		// lazy load
		if (_tracks == null) {
			getTracks();
		}

		long diffInMillies = _tracks.get(_tracks.size() - 1).getCreatedAt()
				.getTime()
				- _tracks.get(0).getCreatedAt().getTime();
		_duration = (double) TimeUnit.SECONDS.convert(diffInMillies,
				TimeUnit.MILLISECONDS);

		return _duration;
	}

}
