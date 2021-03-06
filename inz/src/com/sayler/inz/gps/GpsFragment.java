package com.sayler.inz.gps;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sayler.inz.IlastIntent;
import com.sayler.inz.R;
import com.sayler.inz.data.RoadDataProvider;
import com.sayler.inz.database.DBSqliteOpenHelper;
import com.sayler.inz.database.DaoHelper;
import com.sayler.inz.database.model.Road;
import com.sayler.inz.database.model.Track;
import com.sayler.inz.gps.EndRecordingDialog.EndRecordingDialogListener;
import com.sayler.inz.gps.GpsNotFixedDialog.GpsNotFixedDialogListener;
import com.sayler.inz.gps.TurnOnGpsDialog.TurnOnGpsDialogListener;
import com.sayler.inz.gps.service.RequestUpdateUIEvent;
import com.sayler.inz.gps.service.StartRecordingEvent;
import com.sayler.inz.gps.service.StopRecordingEvent;
import com.sayler.inz.gps.service.UpdateUiEvent;
import com.sayler.inz.gps.service.WorkoutService;
import com.sayler.inz.gps.sports.Calories;
import com.sayler.inz.gps.sports.ISport;
import com.sayler.inz.history.RoadActivity;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.EventBusException;

public class GpsFragment extends SherlockFragment implements OnClickListener,
		GpsNotFixedDialogListener, EndRecordingDialogListener,
		TurnOnGpsDialogListener, OnNavigationListener {

	private String[] sportsList, sportsClasses;

	private final static String TAG = "GpsFragment";

	private FragmentManager fm;

	private LocationManager locationManager;

	private Calories caloriesCalculator;
	private ISport sport;

	private float distance = 0;
	private float calories = 0;

	// ORM
	private Road currentRoad = null;
	private RoadDataProvider roadData = null;

	private Button startButton;
	private Button endButton;

	private TextView gpsStatusView;
	private TextView sportChosenTextView;

	private TextView distanceTextView;
	private TextView caloriesTextView;

	private TimerView timerView;

	private boolean isRecording;
	private boolean isGpsFix = false;

	private GoogleMap map;
	private SupportMapFragment mapFragment;
	private Circle circle = null;
	private LatLng lastLatLng = null;

	private SharedPreferences sharedPref;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.gps_fragment, container, false);

		// FragmentManager
		fm = getSherlockActivity().getSupportFragmentManager();

		// views
		gpsStatusView = (TextView) view.findViewById(R.id.gpsStatusText);
		distanceTextView = (TextView) view.findViewById(R.id.distanceTextView);
		caloriesTextView = (TextView) view.findViewById(R.id.caloriesTextView);
		sportChosenTextView = (TextView) view.findViewById(R.id.sportChosen);
		timerView = (TimerView) view.findViewById(R.id.timerView1);
		startButton = (Button) view.findViewById(R.id.startButton);
		endButton = (Button) view.findViewById(R.id.endButton);

		// buttons
		startButton.setOnClickListener(this);
		endButton.setOnClickListener(this);

		// shared pref
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this
				.getActivity());

		// get sports list and corresponding classes
		sportsList = getResources().getStringArray(R.array.sports_list);
		sportsClasses = getResources().getStringArray(R.array.sports_classes);

		caloriesCalculator = new Calories();
		// load preferences
		loadPrefs();

		// Get the location manager
		locationManager = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);

		// start gps listener
		boolean gpsEnabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);

		// dialog to turn on gps
		if (!gpsEnabled) {
			TurnOnGpsDialog gpsTurnOnDialog = new TurnOnGpsDialog();
			gpsTurnOnDialog.setTargetFragment(this, 0);
			gpsTurnOnDialog.show(fm, "turn_on_gps");
		}

		// ORM
		DaoHelper.setOpenHelper(getActivity().getApplicationContext(),
				DBSqliteOpenHelper.class);
		roadData = new RoadDataProvider();

		// is service is NOT running
		if (!WorkoutService.isRunning()) {
			Intent workoutSe = new Intent(getActivity(), WorkoutService.class);
			getActivity().startService(workoutSe);
		} else {
			Toast.makeText(getActivity(),
					"Service is already running - loading data...",
					Toast.LENGTH_LONG).show();
		}

		// maps stuff
		if (savedInstanceState == null) {
			getChildFragmentManager()
					.beginTransaction()
					.add(R.id.linearLayoutMap, new SupportMapFragment(),
							"MapFragment").commit();
			getChildFragmentManager().executePendingTransactions();

		}
		mapFragment = (SupportMapFragment) getChildFragmentManager()
				.findFragmentById(R.id.linearLayoutMap);

		// when everything else is set request for event
		// register event bus
		try {
			EventBus.getDefault().register(this);
		} catch (EventBusException e) {
			Log.d(TAG, "event bus already registered");
		} finally {
			EventBus.getDefault().post(new RequestUpdateUIEvent());
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
				startRecording();

			break;

		case R.id.endButton: 
			
			// end recording
			showEndRecordingDialog();

			break;
		}

	}

	public void showEndRecordingDialog() {
		// dialog if user is sure
		EndRecordingDialog gpsDialog = new EndRecordingDialog();
		gpsDialog.setTargetFragment(this, 0);
		gpsDialog.show(fm, "end_recording");
	}

	// UpdateUI event
	// if service is recording when user start activity with this fragment
	public void onEventMainThread(final UpdateUiEvent e) {
		Log.d(TAG, " updateUI gps fixed? " + e.isGpsFixed);

		gpsFix(e.isGpsFixed);
		recording(e.isRecording);

		if (e.isRecording) {
			// setting UI controls to match data collected by recording service

			// ORM
			currentRoad = e.currentRoad;

			// set timer view
			timerView.start(e.time);

			// update distance view
			distanceTextView.setText(String.format("%.0f m", e.distance));

			// calories calculation
			calories = caloriesCalculator.calculate(e.distance, (int) e.time,
					sharedPref);

			// update calories view
			caloriesTextView.setText(String.format("%.0f kcal", calories));

			// update variable
			distance = e.distance;

			// get map object
			map = mapFragment.getMap();

			// if map has been created
			if (map == null) {
				Log.d(TAG, "nie ma mapki");
				return;
			}

			// draw road on map
			PolylineOptions roadLineSoFar = new PolylineOptions().width(5)
					.color(Color.RED);

			// ORM
			// get road gps tracks of road so far
			Road rsf;
			try {
				rsf = roadData.get(currentRoad.getId());
				List<Track> tracksSoFar = rsf.getTracks();
				for (Track t : tracksSoFar) {
					roadLineSoFar.add(new LatLng(t.getLat(), t.getLng()));
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			map.addPolyline(roadLineSoFar);

			// move map camera
			float zoomLevel = 16.0f;
			if (lastLatLng != null) {
				zoomLevel = map.getCameraPosition().zoom;
			}
			map.moveCamera(CameraUpdateFactory
					.newCameraPosition(new CameraPosition(new LatLng(e.lat,
							e.lng), zoomLevel, 0, 0)));

			// add accuracy circle
			CircleOptions circOpt = new CircleOptions().radius(e.accuracy)
					.fillColor(Color.argb(150, 0, 0, 250)).strokeWidth(2)
					.strokeColor(Color.argb(250, 0, 0, 250))
					.center(new LatLng(e.lat, e.lng));

			// draw accuracy circle (or move it)
			if (circle == null) {
				circle = map.addCircle(circOpt);
			} else {
				circle.setRadius(e.accuracy);
				circle.setCenter(new LatLng(e.lat, e.lng));
			}

			// save last location
			lastLatLng = new LatLng(e.lat, e.lng);
		}
	}

	private void gpsFix(boolean b) {
		isGpsFix = b;

		if (b)
			gpsStatusView.setText(R.string.gps_fixed);
		else
			gpsStatusView.setText(R.string.gps_not_fixed);
	}

	private void recording(boolean b) {
		isRecording = b;

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

		// ORM
		currentRoad = new Road();
		currentRoad.setCreatedAt(new Date());
		currentRoad.setSport_type(sport.getSportType());
		roadData.save(currentRoad);

		// start timer
		timerView.start();

		// start recording in service
		EventBus.getDefault().post(new StartRecordingEvent(currentRoad));

		recording(true);
	}

	// if user end recording manually
	public void endRecording() {

		// stop recording in service
		EventBus.getDefault().post(new StopRecordingEvent());

		// stop timer
		timerView.end();

		// reset Google Maps drawing
		circle = null;
		lastLatLng = null;

		map = mapFragment.getMap();
		map.clear();

		// Database
		// save roads details into Roads table
		int time = timerView.getElapsedTime();
		double avg_speed = (distance / 1000.0) / (time / 3600.0);

		// ORM
		currentRoad.setAvg_speed(avg_speed);
		currentRoad.setCalories((int) calories);
		currentRoad.setDistance(distance);
		currentRoad.setDuration(time);
		
		
		roadData.save(currentRoad);

		// start Road activity - show the road
		Intent roadActivityIntent = new Intent(getActivity(),
				RoadActivity.class);
		roadActivityIntent.putExtra("roadId", currentRoad.getId());
		startActivity(roadActivityIntent);
		getActivity().overridePendingTransition(R.animator.left_to_right_show,
				R.animator.left_to_right_hide);
	}

	@Override
	public void onGpsNotFixedDialogPositiveClick() {
		startRecording();
	}

	@Override
	public void onEndRecordingDialogPositiveClick() {
		endRecording();

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		// have to be done here because it seems like onCreateOptionsMenu is
		// fired before onCreateView on some Androids (like G S2)
		sportsList = getResources().getStringArray(R.array.sports_list);
		sportsClasses = getResources().getStringArray(R.array.sports_classes);

		Log.d(TAG, "sport list: " + sportsList.toString());
		// create menu with sports to choose
		SubMenu sportsMenu = menu.addSubMenu(Menu.NONE, -1, Menu.NONE,
				R.string.sports_title);
		sportsMenu.getItem().setShowAsAction(2);

		// add items to menu in AB
		int i = 0;
		for (String sport : sportsList) {
			sportsMenu.add(Menu.NONE, i, Menu.NONE, sport);
			i++;
		}
		super.onCreateOptionsMenu(sportsMenu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG,
				"context item selected" + item.getTitle() + " "
						+ item.getItemId());
		// if sport chosen
		if (item.getItemId() >= 0 && item.getItemId() < sportsClasses.length) {
			// change sport
			changeSport(sportsClasses[item.getItemId()]);
		}
		return super.onOptionsItemSelected(item);
	}

	private void changeSport(String sportClassName) {
		String sportName = "";
		try {

			Class<ISport> sportClass = (Class<ISport>) Class
					.forName(sportClassName);
			int i = 0;
			for (String sport : sportsClasses) {
				if (sport.equals(sportClassName))
					break;
				i++;
			}
			sportName = sportsList[i];
			sport = sportClass.newInstance();

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (java.lang.InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		caloriesCalculator.setCaloriesCalculateStrategy(sport);
		sportChosenTextView.setText(sportName);

		// edit shared preference with sport
		Editor editor = sharedPref.edit();
		editor.putString("chosen_sport", sport.getClass().getCanonicalName());
		editor.commit();
	}

	public void loadPrefs() {
		// set previously chosen sport
		String sportClassName = sharedPref.getString("chosen_sport",
				"com.sayler.inz.gps.sports.Running");
		// change sport
		changeSport(sportClassName);
	}

	@Override
	public void onTurnOnGpsDialogPositiveClick() {
		Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivity(intent);
	}

	@Override
	public void onDestroy() {
		// stop timer
		timerView.end();

		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		return false;
	}

	@Override
	public void onResume() {

		// get last intent that started Activity
		Bundle lastIntent = ((IlastIntent) getActivity()).popLastIntentExtras();
		if (lastIntent != null) {
			// check if STOP_RECORDING_INTENT was send
			if (lastIntent.containsKey(WorkoutService.STOP_RECORDING_INTENT)) {
				showEndRecordingDialog();
			}
		}

		super.onResume();
	}

}
