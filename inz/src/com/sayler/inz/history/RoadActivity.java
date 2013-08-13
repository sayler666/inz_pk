package com.sayler.inz.history;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

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
import com.sayler.inz.gps.Tracks;

public class RoadActivity extends SherlockFragmentActivity {
	private GoogleMap map;

	private static String TAG = "RoadActivity";
	private Database db;
	private long roadId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// set layout
		setContentView(R.layout.road_activity);

		// intent
		Intent intent = getIntent();
		roadId = intent.getLongExtra("roadId", 0);
		Log.d(TAG, " roadid  " + roadId);

		// database
		db = new Database(this);

		// get road gps tracks
		final Cursor roadCur = db.getRoadById(roadId);

		// draw road on map
		PolylineOptions roadLine = new PolylineOptions().width(5).color(
				Color.RED);

		// bounds - need to center map over road
		final LatLngBounds.Builder bc = new LatLngBounds.Builder();

		// get latlng from cursor
		while (roadCur.moveToNext()) {
			double lat = roadCur.getDouble(roadCur
					.getColumnIndex(Tracks.COLUMN_LAT));
			double lng = roadCur.getDouble(roadCur
					.getColumnIndex(Tracks.COLUMN_LNG));

			LatLng ll = new LatLng(lat, lng);

			roadLine.add(ll);
			bc.include(ll);

			Log.d(TAG, " lat:lng " + lat + " " + lng);
		}

		// maps stuff
		GoogleMapOptions op = new GoogleMapOptions();
		op.camera(new CameraPosition(new LatLng(50.1243, 19.1243), 11, 0, 0));
		map = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		map.addPolyline(roadLine);
		map.setOnCameraChangeListener(new OnCameraChangeListener() {

			@Override
			public void onCameraChange(CameraPosition position) {
				if (roadCur.getCount() > 0) {
					// Move camera.
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

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
