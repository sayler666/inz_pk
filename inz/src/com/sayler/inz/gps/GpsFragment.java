package com.sayler.inz.gps;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.sayler.inz.R;

public class GpsFragment extends Fragment implements OnClickListener,
		LocationListener {

	private LocationManager locationManager;
	private long mLastLocationMillis;
	private Location mLastLocation;
	private boolean isGpsFix = false;
	private ToggleButton gpsToggleButton;
	private TextView gpsStatusView;
	private TextView gpsLngLanView;
	private View controlsLayout;
	private boolean isRecording;

	private Button startButton;
	private Button endButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.gps_fragment, container, false);

		gpsToggleButton = (ToggleButton) view
				.findViewById(R.id.toggleGpsButton);
		gpsToggleButton.setOnClickListener(this);

		gpsStatusView = (TextView) view.findViewById(R.id.gpsStatusText);
		gpsLngLanView = (TextView) view.findViewById(R.id.gpsLngLanTextView);

		controlsLayout = view.findViewById(R.id.controlsLayout);
		startButton = (Button) view.findViewById(R.id.startButton);
		endButton = (Button) view.findViewById(R.id.endButton);
		startButton.setOnClickListener(this);
		endButton.setOnClickListener(this);

		// Get the location manager
		locationManager = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);

		return view;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		// gps button
		case R.id.toggleGpsButton:
			boolean on = ((ToggleButton) v).isChecked();
			if (on) {

				boolean enabled = locationManager
						.isProviderEnabled(LocationManager.GPS_PROVIDER);

				if (!enabled) {
					Intent intent = new Intent(
							Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivity(intent);
				}

				locationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, 400, 1, this);
				Toast.makeText(getActivity(), LocationManager.GPS_PROVIDER, 10)
						.show();
				locationManager.addGpsStatusListener(mGPSListener);
			} else {
				gpsFix(false);

				locationManager.removeUpdates(this);
				Toast.makeText(getActivity(), LocationManager.GPS_PROVIDER, 10)
						.show();
				locationManager.removeGpsStatusListener(mGPSListener);
			}

			// Database db = new Database(getActivity());
			// Tracks tr = new Tracks(10, 1, 13, 123);
			//
			// Toast.makeText(getActivity(), String.valueOf(db.addTrack(tr)),
			// 10)
			// .show();

			break;

		case R.id.startButton:
			Log.d(this.getClass().toString(), "start button");
			this.isRecording = true;

			startButton.setVisibility(View.GONE);
			endButton.setVisibility(View.VISIBLE);

			// TODO generate new id raod
				
			break;

		case R.id.endButton:
			this.isRecording = false;

			startButton.setVisibility(View.VISIBLE);
			endButton.setVisibility(View.GONE);

			// TODO save all data to db
			
			break;
		}

	}

	private void gpsFix(boolean b) {
		this.isGpsFix = b;
		ObjectAnimator controlsAnimator;
		if (b) { // is fixed
			gpsStatusView.setText("Gps fixed!");

			controlsLayout.setVisibility(View.VISIBLE);
			controlsLayout.setAlpha(0);

			controlsAnimator = ObjectAnimator.ofFloat(controlsLayout, "alpha",
					1);
			controlsAnimator.setDuration(1000);
			controlsAnimator.start();
		} else {
			gpsStatusView.setText("Gps not fixed!");

			controlsAnimator = ObjectAnimator.ofFloat(controlsLayout, "alpha",
					0);
			controlsAnimator.setDuration(500);
			controlsAnimator.start();

		}
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

				if (isGpsFix) { 		// A fix has been acquired.
					gpsFix(true);
				} else { 				// The fix has been lost.
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

		if (location == null)
			return;
		mLastLocationMillis = SystemClock.elapsedRealtime();
		mLastLocation = location;

		int lat = (int) (location.getLatitude());
		int lng = (int) (location.getLongitude());
		Log.d(this.getClass().toString(), " location change: " + lat + " "
				+ lng);
		gpsLngLanView.append(lat + " " + lng + "\n");
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

}
