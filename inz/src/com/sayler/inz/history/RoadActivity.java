package com.sayler.inz.history;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sayler.inz.R;
import com.sayler.inz.gps.Database;
import com.sayler.inz.gps.Roads;
import com.sayler.inz.gps.TimerView;
import com.sayler.inz.gps.Tracks;

public class RoadActivity extends SherlockFragmentActivity {
	private GoogleMap map;

	private static String TAG = "RoadActivity";
	private Database db;
	private long roadId;

	private TextView distanceTextView;
	private TextView caloriesTextView;
	private TimerView timerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// set layout
		setContentView(R.layout.road_activity);

		// intent
		Intent intent = getIntent();
		roadId = intent.getLongExtra("roadId", 0);

		// database
		db = new Database(this);

		// get road gps tracks
		final Cursor roadCur = db.getRoadById(roadId);

		// draw road on map
		PolylineOptions roadLine = new PolylineOptions().width(5).color(
				Color.RED);

		// bounds - need to center map over road
		final LatLngBounds.Builder bc = new LatLngBounds.Builder();

		// get latlng from Cursor
		while (roadCur.moveToNext()) {
			double lat = roadCur.getDouble(roadCur
					.getColumnIndex(Tracks.COLUMN_LAT));
			double lng = roadCur.getDouble(roadCur
					.getColumnIndex(Tracks.COLUMN_LNG));

			LatLng ll = new LatLng(lat, lng);

			roadLine.add(ll);
			bc.include(ll);
		}

		// maps stuff
		//GoogleMapOptions op = new GoogleMapOptions();
		//op.camera(new CameraPosition(new LatLng(50.1243, 19.1243), 11, 0, 0));
		map = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		map.addPolyline(roadLine);
		map.setOnCameraChangeListener(new OnCameraChangeListener() {

			@Override
			public void onCameraChange(CameraPosition position) {
				if (roadCur.getCount() > 0) {
					// Move camera
					map.moveCamera(CameraUpdateFactory.newLatLngBounds(
							bc.build(), 50));
					// Remove listener to prevent position reset on camera move.
					map.setOnCameraChangeListener(null);
				}

			}
		});

		// action bar back
		ActionBar ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);

		// road info
		roadCur.moveToFirst();

		distanceTextView = (TextView) findViewById(R.id.distanceTextView);
		caloriesTextView = (TextView) findViewById(R.id.caloriesTextView);
		timerView = (TimerView) findViewById(R.id.timerView1);

		try {
			timerView.setTime((long) roadCur.getDouble(roadCur
					.getColumnIndex(Roads.COLUMN_DURATION)));
			caloriesTextView.setText(roadCur.getDouble(roadCur
					.getColumnIndex(Roads.COLUMN_CALORIES)) + " kcal");
			distanceTextView.setText(roadCur.getDouble(roadCur
					.getColumnIndex(Roads.COLUMN_DISTANCE)) + " m");

		} catch (Exception e) {
			Log.d(TAG, "exception");
			e.printStackTrace();
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.onBackPressed();
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {

		super.onBackPressed();
		overridePendingTransition(R.animator.right_to_left_show,
				R.animator.right_to_left_hide);
	}

}
