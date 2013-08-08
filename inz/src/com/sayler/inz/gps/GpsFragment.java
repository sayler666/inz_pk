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
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.sayler.inz.R;
import com.sayler.inz.gps.EndRecordingDialog.EndRecordingDialogListener;
import com.sayler.inz.gps.GpsNotFixedDialog.GpsNotFixedDialogListener;
import com.sayler.inz.gps.TurnOnGpsDialog.TurnOnGpsDialogListener;
import com.sayler.inz.gps.service.UpdateUiEvent;
import com.sayler.inz.gps.service.WorkoutService;
import com.sayler.inz.gps.sports.Calories;
import com.sayler.inz.gps.sports.ISport;
import com.sayler.inz.gps.sports.Running;

import de.greenrobot.event.EventBus;

public class GpsFragment extends SherlockFragment implements OnClickListener,
		LocationListener, GpsNotFixedDialogListener, EndRecordingDialogListener,TurnOnGpsDialogListener {


	private final static String TAG = "GpsFragment";

	private FragmentManager fm;

	private LocationManager locationManager;
	private Location mLastLocation = null;
	private long mLastLocationMillis;

	private Calories caloriesCalculator = new Calories();
	private ISport sport;

	private float distance = 0;
	private float calories;

	private Database gpsDb;

	private long currentRoadId = -1;

	private Button startButton;
	private Button endButton;

	private TextView gpsStatusView;

	private TextView distanceTextView;
	private TextView caloriesTextView;

	private TimerView timerView;

	private boolean isRecording;
	private boolean isGpsFix = false;



	@Override
	public void onCreate(Bundle savedInstanceState) {



		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.gps_fragment, container, false);

		gpsStatusView = (TextView) view.findViewById(R.id.gpsStatusText);
		distanceTextView = (TextView) view.findViewById(R.id.distanceTextView);
		caloriesTextView = (TextView) view.findViewById(R.id.caloriesTextView);

		fm = getSherlockActivity().getSupportFragmentManager();

		startButton = (Button) view.findViewById(R.id.startButton);
		endButton = (Button) view.findViewById(R.id.endButton);
		startButton.setOnClickListener(this);
		endButton.setOnClickListener(this);

		timerView = (TimerView) view.findViewById(R.id.timerView1);

		// Get the location manager
		locationManager = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);

		// start gps listener
		boolean enabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);

		if (!enabled) {
			// dialog to turn on gps
			TurnOnGpsDialog gpsTurnOnDialog = new TurnOnGpsDialog();
			gpsTurnOnDialog.setTargetFragment(this, 0);
			gpsTurnOnDialog.show(fm, "turn_on_gps");
			
		}

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				1000, 3, this);

		locationManager.addGpsStatusListener(mGPSListener);

		// instance of Db
		gpsDb = new Database(getActivity().getApplicationContext());

		// TODO choosing sport type
		sport = new Running();

		return view;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.startButton: // start recording

			// if GPS not fix - dialog
			if (isGpsFix == false) {

				GpsNotFixedDialog gpsDialog = new GpsNotFixedDialog();
				gpsDialog.setTargetFragment(this, 0);
				gpsDialog.show(fm, "gps_dialog");
			} else
				this.startRecording();

			break;

		case R.id.endButton: // end recording

			// dialog if user is sure
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

	public void onEventMainThread(UpdateUiEvent e){
		Log.d(TAG,"update");
		distanceTextView.append("a");
	}

	
	// all recording goes here
	public void startRecording() {
		this.isRecording = true;

		if(!WorkoutService.isRunning()){
			Intent workoutSe = new Intent(getActivity(),WorkoutService.class);
			getActivity().startService(workoutSe);
			
			EventBus.getDefault().register(this);
		}else{
			Toast.makeText(getActivity(), "service is running already",Toast.LENGTH_LONG).show();
		}
		
		
		
		// show necessary buttons
		startButton.setVisibility(View.GONE);
		endButton.setVisibility(View.VISIBLE);

		// generate new road id (highest id + 1)
		currentRoadId = gpsDb.getNexRoadId();

		// reset distance
		distance = 0;
		distanceTextView.setText(String.format("%.0f m", distance));

		// choose sport
		caloriesCalculator.setCaloriesCalculateStrategy(sport);

		// start timer
		timerView.start();
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
		// update distance view
		distanceTextView.setText(String.format("%.0f m", distance));

		// calories calculation
		calories = caloriesCalculator.calculate(distance, 75, 1,
				timerView.getElapsedTime());
		// update calories view
		caloriesTextView.setText(String.format("%.0f kcal", calories));

		//
		// Database
		// save track to database
		Tracks track = new Tracks(lat, lng, alt, speed, time,
				this.currentRoadId);
		gpsDb.addTrack(track);

		// remember last location
		mLastLocation = location;
	}

	public void endRecording() {
		this.isRecording = false;

		// hide necessary buttons
		startButton.setVisibility(View.VISIBLE);
		endButton.setVisibility(View.GONE);

		// stop timer
		timerView.end();

		//
		// Database
		// save roads details into Roads table
		int time = timerView.getElapsedTime();
		double avg_speed = (distance / 1000.0) / (time / 3600.0);
		Roads newRoad = new Roads(distance, time, avg_speed, (int) calories,
				this.currentRoadId);
		gpsDb.addRoad(newRoad);
	}

	@Override
	public void onGpsNotFixedDialogPositiveClick() {
		startRecording();
	}

	@Override
	public void onEndRecordingDialogPositiveClick() {
		endRecording();

	}

	private final int ID_MENU_EXIT = 1;

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.gps_fragment_menu, menu);

		MenuItem item = menu.add(Menu.NONE, ID_MENU_EXIT, Menu.NONE, "test")
				.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(this.getClass().toString(),
				"context item selected" + item.getTitle());
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTurnOnGpsDialogPositiveClick() {
		Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivity(intent);
	}

}
