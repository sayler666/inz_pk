package com.sayler.inz.gps.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.sayler.inz.gps.Database;
import com.sayler.inz.gps.Tracks;

import de.greenrobot.event.EventBus;

//TODO update ui even if gps is no on

public class WorkoutService extends Service implements LocationListener {
	final static String TAG = "WorkoutService";

	private LocationManager locationManager;

	private Location mLastLocation = null;
	private long mLastLocationMillis;

	// ui variables
	private float distance = 0;
	private long time = 0;
	private boolean isGpsFix = false;

	// database
	private Database gpsDb;
	private long currentRoadId = -1;

	// static field

	static boolean isRecording = false;
	static boolean isRunning = false;

	/**
	 * if recording tarcks
	 */
	public static boolean isRecording() {
		return isRecording;
	}

	/**
	 * if service running set to true immediately onStartCommand
	 */
	public static boolean isRunning() {
		return isRunning;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		isRunning = true;

		// Get the location manager
		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		// immediately listen for location update - for fixing gps
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				1000, 3, this);

		// start gps status listener
		locationManager.addGpsStatusListener(mGPSListener);

		// database
		gpsDb = new Database(this.getApplicationContext());

		// register event bus
		EventBus.getDefault().register(this);

		return START_STICKY;
	}

	/**
	 * Start recording event
	 * 
	 * @param e
	 *            contains currentRoadId
	 */
	public void onEvent(StartRecordingEvent e) {
		// set road id
		this.currentRoadId = e.currentRoadId;
		// start recording tracks

		// reset variable
		time = (long) System.currentTimeMillis();
		Log.d(TAG, "onEvent start recording " + currentRoadId);
		distance = 0;

		// recording is on
		isRecording = true;

	}

	/**
	 * Stop recording event
	 * 
	 * @param e
	 *            empty
	 */
	public void onEvent(StopRecordingEvent e) {

		Log.d(TAG, "onEvent stop recording " + currentRoadId);

		// stop recording
		isRecording = false;
		// last UI update
		this.updateUI();

	}
	
	/**
	 * Request update event
	 * 
	 * @param e
	 *            empty
	 */
	public void onEvent(RequestUpdateUIEvent e) {

		Log.d(TAG, "onEventRequestUpdateUI ");

		
		// last UI update
		this.updateUI();

	}

	@Override
	public void onLocationChanged(Location location) {
		// GPS fixing stuff
		if (location == null)
			return;

		mLastLocationMillis = SystemClock.elapsedRealtime();

		// if not recording - do not bother about rest
		if (isRecording == false)
			return;

		double lat = location.getLatitude();
		double lng = location.getLongitude();
		float speed = location.getSpeed();
		long time = location.getTime();
		double alt = location.getAltitude();

		// calculate distance
		if (mLastLocation != null) {
			float[] results = new float[5];
			Location.distanceBetween(mLastLocation.getLatitude(),
					mLastLocation.getLongitude(), location.getLatitude(),
					location.getLongitude(), results);
			distance += results[0];
		}

		//
		// Database
		// save track to database
		Tracks track = new Tracks(lat, lng, alt, speed, time,
				this.currentRoadId);
		gpsDb.addTrack(track);

		// remember last location
		mLastLocation = location;

		// Update ui
		this.updateUI();
	}

	public void updateUI() {

		// send event to UPDATE UI
		EventBus.getDefault().post(
				new UpdateUiEvent(distance, time, isGpsFix, isRecording,
						currentRoadId));
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.d(this.getClass().toString(), provider + "  enabled ");
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy" + " idRoad " + currentRoadId);
		locationManager.removeUpdates(this);
		super.onDestroy();
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.d(this.getClass().toString(), provider + "  disable ");
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.d(this.getClass().toString(), " status changed: ");
	}

	// GPS status
	private GpsStatus.Listener mGPSListener = new GpsStatus.Listener() {
		@Override
		public void onGpsStatusChanged(final int event) {
			switch (event) {
			case GpsStatus.GPS_EVENT_STARTED:
				break;
			case GpsStatus.GPS_EVENT_FIRST_FIX:
				isGpsFix = true;

				break;
			case GpsStatus.GPS_EVENT_STOPPED:

				break;
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
				if (mLastLocation != null)
					isGpsFix = (SystemClock.elapsedRealtime() - mLastLocationMillis) < 10000;

				break;
			default:
				break;
			}
			updateUI();
		}
	};

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}
