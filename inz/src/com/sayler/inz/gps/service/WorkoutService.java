package com.sayler.inz.gps.service;

import java.util.Date;

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

import com.sayler.inz.data.TrackDataProvider;
import com.sayler.inz.database.DBSqliteOpenHelper;
import com.sayler.inz.database.DaoHelper;
import com.sayler.inz.database.model.Road;
import com.sayler.inz.database.model.Track;

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
	private double lat, lng;
	private float accuracy;

	// ORM
	private Road currentRoad = null;

	// static field
	static boolean isRecording = false;
	static boolean isRunning = false;

	// -- miscellaneous

	// if accuracy above do not save track
	private final int minimumAccuracy = 20;

	/**
	 * if recording track
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

		// immediately listen for location update - for fixing GPS
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				1000, 3, this);

		// start GPS status listener
		locationManager.addGpsStatusListener(mGPSListener);

		// ORM
		DaoHelper.setOpenHelper(this.getApplicationContext(),
				DBSqliteOpenHelper.class);

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

		// start recording tracks

		this.currentRoad = e.currentRoad;

		// reset variable
		time = (long) System.currentTimeMillis();
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

		// UI update
		this.updateUI();

	}

	@Override
	public void onLocationChanged(Location location) {
		// GPS fixing stuff
		if (location == null)
			return;

		mLastLocationMillis = SystemClock.elapsedRealtime();

		// if not recording - do not bother about rest calculations, but update
		// UI
		// (maybe gps've been fixed)
		if (isRecording == false) {
			this.updateUI();
			return;

		}
		// check accuracy
		// TODO if accuracy < minimum_accuracy don't save track

		lat = location.getLatitude();
		lng = location.getLongitude();
		accuracy = location.getAccuracy();

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

		// ORM
		Track trackOrm = new Track(lat, lng, alt, speed, time, this.currentRoad);
		trackOrm.setCreatedAt(new Date());
		TrackDataProvider trackData = new TrackDataProvider();
		trackData.save(trackOrm);

		// remember last location
		mLastLocation = location;

		// Update UI
		this.updateUI();
	}

	public void updateUI() {

		// send event to UPDATE UI
		EventBus.getDefault().post(
				new UpdateUiEvent(distance, time, isGpsFix, isRecording, lat, lng, accuracy, currentRoad));
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.d(this.getClass().toString(), provider + "  enabled ");
	}

	@Override
	public void onDestroy() {
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
				// TODO something wrong here on 4.2.2
				// isGpsFix = true;
				// Log.d(TAG, "fixed: ");
				break;
			case GpsStatus.GPS_EVENT_STOPPED:

				break;
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
				if (mLastLocation != null)
					isGpsFix = (SystemClock.elapsedRealtime() - mLastLocationMillis) < 3000;

				break;
			default:
				break;
			}
			updateUI();
		}
	};

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}
