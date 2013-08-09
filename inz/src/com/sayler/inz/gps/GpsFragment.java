package com.sayler.inz.gps;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.ContactsContract.CommonDataKinds.Event;
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
import com.sayler.inz.gps.service.StartRecordingEvent;
import com.sayler.inz.gps.service.StopRecordingEvent;
import com.sayler.inz.gps.service.UpdateUiEvent;
import com.sayler.inz.gps.service.WorkoutService;
import com.sayler.inz.gps.sports.Calories;
import com.sayler.inz.gps.sports.ISport;
import com.sayler.inz.gps.sports.Running;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.EventBusException;

public class GpsFragment extends SherlockFragment implements OnClickListener,
		GpsNotFixedDialogListener, EndRecordingDialogListener,
		TurnOnGpsDialogListener {

	private final static String TAG = "GpsFragment";

	private FragmentManager fm;

	private LocationManager locationManager;

	private Calories caloriesCalculator = new Calories();
	private ISport sport;

	private float distance = 0;
	private float calories = 0;

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

		// TODO choosing sport type
		sport = new Running();
		// choose sport
		caloriesCalculator.setCaloriesCalculateStrategy(sport);

		// instance of Db
		gpsDb = new Database(getActivity().getApplicationContext());

		// is service running
		if (!WorkoutService.isRunning()) {
			Intent workoutSe = new Intent(getActivity(), WorkoutService.class);
			getActivity().startService(workoutSe);

		} else {
			Toast.makeText(getActivity(), "Service is already running - loading data...",
					Toast.LENGTH_LONG).show();
		}

		// register event bus
		try {
			EventBus.getDefault().register(this);
		} catch (EventBusException e) {
			Log.d(TAG, "event bus already registered");
		}

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

	// UpdateUI event
	// if service is recording when user start activity with this fragment
	public void onEventMainThread(UpdateUiEvent e) {
		//Log.d(TAG, " updateUI gps fixed? " + e.isGpsFixed);

		this.gpsFix(e.isGpsFixed);
		this.recording(e.isRecording);

		if (e.isRecording) {
			// setting UI controls to match data collected by recording service

			// set timer view
			this.timerView.start(e.time);

			// update distance view
			distanceTextView.setText(String.format("%.0f m", e.distance));

			// calories calculation
			float cal = caloriesCalculator.calculate(e.distance, 75, 1,
					(int) e.time);
			// update calories view
			caloriesTextView.setText(String.format("%.0f kcal", cal));

			// roadId from service
			this.currentRoadId = e.currentRoadId;
		}
	}

	private void gpsFix(boolean b) {
		this.isGpsFix = b;

		if (b)
			gpsStatusView.setText(R.string.gps_fixed);
		else
			gpsStatusView.setText(R.string.gps_not_fixed);
	}

	private void recording(boolean b) {
		this.isRecording = b;

		if (b) {
			// show necessary buttons
			startButton.setVisibility(View.GONE);
			endButton.setVisibility(View.VISIBLE);
		} else {
			// hide necessary buttons
			startButton.setVisibility(View.VISIBLE);
			endButton.setVisibility(View.GONE);
		}
	}

	// if user start recording manually
	public void startRecording() {

		// generate new road id (highest id + 1)
		currentRoadId = gpsDb.getNexRoadId();

		// start timer
		timerView.start();

		// start recording in service
		EventBus.getDefault().post(new StartRecordingEvent(currentRoadId));
		
		this.recording(true);
	}

	// if user end recording manually
	public void endRecording() {

		// stop recording in service
		EventBus.getDefault().post(new StopRecordingEvent());

		// stop timer
		timerView.end();
		Log.d(TAG, "STOP TIMER!");
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

	// private final int ID_MENU_EXIT = 1;

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.gps_fragment_menu, menu);

		/*
		 * MenuItem item = menu.add(Menu.NONE, ID_MENU_EXIT, Menu.NONE, "test")
		 * .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		 */

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

	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

}
