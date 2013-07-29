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
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.gps_fragment, container, false);

		gpsToggleButton = (ToggleButton) view
				.findViewById(R.id.toggleGpsButton);
		gpsToggleButton.setOnClickListener(this);

		gpsStatusView = (TextView) view.findViewById(R.id.gpsStatusText);
		gpsLngLanView = (TextView) view.findViewById(R.id.gpsLngLanTextView);

		// Get the location manager
		locationManager = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);

		boolean enabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);

		if (!enabled) {
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(intent);
		}

		return view;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.toggleGpsButton:
			boolean on = ((ToggleButton) v).isChecked();
			if (on) {
				locationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, 400, 1, this);
				Toast.makeText(getActivity(), LocationManager.GPS_PROVIDER, 10)
						.show();
				locationManager.addGpsStatusListener(mGPSListener);
			} else {
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

		}

	}

	private GpsStatus.Listener mGPSListener = new GpsStatus.Listener() {
		@Override
		public void onGpsStatusChanged(final int event) {
			switch (event) {
			case GpsStatus.GPS_EVENT_STARTED:
				break;
			case GpsStatus.GPS_EVENT_FIRST_FIX:
				
				isGpsFix = true;
				gpsStatusView.setText("Gps fixed!");
				break;
			case GpsStatus.GPS_EVENT_STOPPED:

				break;
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
				if (mLastLocation != null)
					isGpsFix = (SystemClock.elapsedRealtime() - mLastLocationMillis) < 5000;

				if (isGpsFix) { // A fix has been acquired.
					gpsStatusView.setText("Gps fixed!");
				} else { // The fix has been lost.
					gpsStatusView.setText("Gps fix lost!");
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
