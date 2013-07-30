package com.sayler.inz.gps;

import android.content.Context;
import android.content.Intent;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.sayler.inz.R;
import com.sayler.inz.gps.EndRecordingDialog.EndRecordingDialogListener;
import com.sayler.inz.gps.GpsNotFixedDialog.GpsNotFixedDialogListener;

public class GpsFragment extends SherlockFragment implements OnClickListener,
		LocationListener, GpsNotFixedDialogListener, EndRecordingDialogListener {

	private FragmentManager fm;

	private LocationManager locationManager;
	private Location mLastLocation;
	private long mLastLocationMillis;

	private Database gpsDb;

	private long currentRoadId = -1;

	private Button startButton;
	private Button endButton;

	private TextView gpsStatusView;
	private TextView gpsLngLanView;

	private boolean isRecording;
	private boolean isGpsFix = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.gps_fragment, container, false);

		gpsStatusView = (TextView) view.findViewById(R.id.gpsStatusText);
		gpsLngLanView = (TextView) view.findViewById(R.id.gpsLngLanTextView);

		fm = getSherlockActivity().getSupportFragmentManager();

		startButton = (Button) view.findViewById(R.id.startButton);
		endButton = (Button) view.findViewById(R.id.endButton);
		startButton.setOnClickListener(this);
		endButton.setOnClickListener(this);

		// Get the location manager
		locationManager = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);

		// start gps listener
		boolean enabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);

		if (!enabled) {
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(intent);
		}

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				400, 1, this);
		Toast.makeText(getActivity(), LocationManager.GPS_PROVIDER, Toast.LENGTH_SHORT).show();
		locationManager.addGpsStatusListener(mGPSListener);

		// instance of Db
		gpsDb = new Database(getActivity().getApplicationContext());

		return view;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.startButton: // start recording

			// if GPS not fix - alert
			if (isGpsFix == false) {
				GpsNotFixedDialog gpsDialog = new GpsNotFixedDialog();
				gpsDialog.setTargetFragment(this, 0);
				gpsDialog.show(fm, "gps_dialog");

			} else {
				this.startRecording();
			}

			break;

		case R.id.endButton: // end recording

			// dialog if user sure
			EndRecordingDialog gpsDialog = new EndRecordingDialog();
			gpsDialog.setTargetFragment(this, 0);
			gpsDialog.show(fm, "end_recording");

			break;
		}

	}

	private void gpsFix(boolean b) {
		this.isGpsFix = b;

		if (b)
			gpsStatusView.setText(R.string.gps_fixed);
		else
			gpsStatusView.setText(R.string.gps_not_fixed);
	}

	private GpsStatus.Listener mGPSListener = new GpsStatus.Listener() {
		@Override
		public void onGpsStatusChanged(final int event) {
			switch (event) {
			case GpsStatus.GPS_EVENT_STARTED:
				break;
			case GpsStatus.GPS_EVENT_FIRST_FIX:

				gpsFix(true);
				break;
			case GpsStatus.GPS_EVENT_STOPPED:

				break;
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
				if (mLastLocation != null)
					isGpsFix = (SystemClock.elapsedRealtime() - mLastLocationMillis) < 10000;

				if (isGpsFix) { // A fix has been acquired.
					gpsFix(true);
				} else { // The fix has been lost.
					gpsFix(false);
				}
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onLocationChanged(Location location) {

		if (isRecording == false)
			return;
		if (location == null)
			return;
		mLastLocationMillis = SystemClock.elapsedRealtime();
		mLastLocation = location;

		double lat =  location.getLatitude();
		double lng =  location.getLongitude();
		float speed =location.getSpeed();
		long time = location.getTime();

		Log.d(this.getClass().toString(), " location change: " + lat + " "
				+ lng + ", speed " + speed);

		gpsLngLanView.append(lat + " " + lng + " speed " + speed + " time "
				+ time + "\n");

		// TODO save track
		Tracks track = new Tracks(lat, lng, speed, time, this.currentRoadId);
		gpsDb.addTrack(track);
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.d(this.getClass().toString(), provider + "  enabled ");

	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.d(this.getClass().toString(), provider + "  disable ");

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.d(this.getClass().toString(), " status changed: ");

	}

	// all recording goes here
	public void startRecording() {
		this.isRecording = true;

		startButton.setVisibility(View.GONE);
		endButton.setVisibility(View.VISIBLE);

		currentRoadId = gpsDb.getNexRoadId();
		Log.d(this.getClass().toString(), " currentRoadId: " + currentRoadId);

	}

	public void endRecording() {

		this.isRecording = false;

		startButton.setVisibility(View.VISIBLE);
		endButton.setVisibility(View.GONE);

		// TODO save all data to db
	}

	@Override
	public void onGpsNotFixedDialogPositiveClick() {
		startRecording();
	}

	@Override
	public void onEndRecordingDialogPositiveClick() {
		endRecording();

	}

}
