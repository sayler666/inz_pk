package com.sayler.inz.gps.service;

import java.util.Date;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.sayler.inz.Launch;
import com.sayler.inz.R;
import com.sayler.inz.data.TrackDataProvider;
import com.sayler.inz.database.DBSqliteOpenHelper;
import com.sayler.inz.database.DaoHelper;
import com.sayler.inz.database.model.Road;
import com.sayler.inz.database.model.Track;

import de.greenrobot.event.EventBus;

//TODO update ui even if gps is no on



public class WorkoutService extends Service implements LocationListener {
	final static String TAG = "WorkoutService";
	
	public final static String STOP_RECORDING_INTENT = "stopRecording";
	
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
	// private final int minimumAccuracy = 20;

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

		// running
		isRunning = true;

		// Get the location manager
		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		// immediately listen for location update - for fixing GPS
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				1000, 1, this);

		// start GPS status listener
		locationManager.addGpsStatusListener(mGPSListener);

		// ORM
		DaoHelper.setOpenHelper(getApplicationContext(),
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

	@SuppressLint("NewApi")
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void onEvent(StartRecordingEvent e) {

		// notification
		
		//return to app intent
		Intent intentToReturn = new Intent(getApplicationContext(),
				Launch.class);
		intentToReturn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		PendingIntent pIntentToReturn = PendingIntent.getActivity(
				getApplicationContext(), 0, intentToReturn, PendingIntent.FLAG_UPDATE_CURRENT);
		
		//stop recording intent
		Intent intentToStop = new Intent(getApplicationContext(),
				Launch.class);
		intentToStop.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		
		//stop recording
		intentToStop.putExtra(STOP_RECORDING_INTENT, "1");
		
		//switch fragment
		intentToStop.putExtra(Launch.SWITCH_FRAGMENT_INTENT, "com.sayler.inz.gps.GpsFragment");
		
		PendingIntent pIntentToStop = PendingIntent.getActivity(
				getApplicationContext(), 1, intentToStop, PendingIntent.FLAG_UPDATE_CURRENT);
		
		Notification n = new Notification.Builder(getApplicationContext())
				.setContentTitle(getResources().getText(R.string.app_name))
				.setContentText(getResources().getText(R.string.running))
				.setSmallIcon(R.drawable.ic_drawer)
				.setContentIntent(pIntentToReturn)
				.setAutoCancel(false)
				.addAction(R.drawable.ic_drawer, getResources().getText(R.string.end_button), pIntentToStop).build();

		// service will be in foreground - harder to kill
		startForeground(1, n);

		// current road instance
		currentRoad = e.currentRoad;

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
		
		// remove notification
		stopForeground(true);
		
		// stop recording
		isRecording = false;
		// last UI update
		updateUI();

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
		updateUI();

	}

	@Override
	public void onLocationChanged(Location location) {
		Log.d(TAG, "location changed ");
		// GPS fixing stuff
		if (location == null)
			return;

		mLastLocationMillis = SystemClock.elapsedRealtime();

		// if not recording - do not bother about rest calculations, but update
		// UI (maybe gps've been fixed)
		if (isRecording == false) {
			updateUI();
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
		Track trackOrm = new Track(lat, lng, alt, speed, time, currentRoad);
		trackOrm.setCreatedAt(new Date());
		TrackDataProvider trackData = new TrackDataProvider();
		trackData.save(trackOrm);

		// remember last location
		mLastLocation = location;

		// Update UI
		updateUI();
	}

	public void updateUI() {
		
		// send event to UPDATE UI
		EventBus.getDefault().post(
				new UpdateUiEvent(distance, time, isGpsFix, isRecording, lat,
						lng, accuracy, currentRoad));
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.d(getClass().toString(), provider + "  enabled ");
	}

	@Override
	public void onDestroy() {
		locationManager.removeUpdates(this);
		super.onDestroy();
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.d(getClass().toString(), provider + "  disable ");
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.d(getClass().toString(), " status changed: ");
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
//				 isGpsFix = true;
//				 Log.d(TAG, "fixed: ");
				break;
			case GpsStatus.GPS_EVENT_STOPPED:

				break;
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
				if (mLastLocation != null) {
					isGpsFix = (SystemClock.elapsedRealtime() - mLastLocationMillis) < 3000;
					Log.d(TAG, "GPS SATELLITE STATUS : " + isGpsFix);
				}

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
