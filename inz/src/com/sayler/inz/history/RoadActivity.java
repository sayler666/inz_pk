package com.sayler.inz.history;

import java.sql.SQLException;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sayler.inz.R;
import com.sayler.inz.data.RoadDataProvider;
import com.sayler.inz.database.DBSqliteOpenHelper;
import com.sayler.inz.database.DaoHelper;
import com.sayler.inz.database.model.Road;
import com.sayler.inz.database.model.Track;
import com.sayler.inz.gps.TimerView;

public class RoadActivity extends SherlockFragmentActivity {
	private GoogleMap map;

	private static final String TAG = "RoadActivity";
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

		// draw road on map
		PolylineOptions roadLine = new PolylineOptions().width(5).color(
				Color.RED);

		// bounds - need to center map over road
		final LatLngBounds.Builder bc = new LatLngBounds.Builder();

		// ORM
		DaoHelper.setOpenHelper(getApplicationContext(),
				DBSqliteOpenHelper.class);
		RoadDataProvider roadData = new RoadDataProvider();

		try {
			final Road road = roadData.get(roadId);

			for (Track t : road.getTracks()) {
				LatLng ll = new LatLng(t.getLat(), t.getLng());
				roadLine.add(ll);
				bc.include(ll);
			}

			// maps stuff
			map = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			map.addPolyline(roadLine);
			map.setOnCameraChangeListener(new OnCameraChangeListener() {

				@Override
				public void onCameraChange(CameraPosition position) {
					if (road.getTracks().size() > 0) {
						// Move camera
						map.moveCamera(CameraUpdateFactory.newLatLngBounds(
								bc.build(), 50));
						// Remove listener to prevent position reset on camera
						// move.
						map.setOnCameraChangeListener(null);
					}

				}
			});

			// action bar back
			ActionBar ab = getSupportActionBar();
			ab.setDisplayHomeAsUpEnabled(true);

			// road info

			distanceTextView = (TextView) findViewById(R.id.distanceTextView);
			caloriesTextView = (TextView) findViewById(R.id.caloriesTextView);
			timerView = (TimerView) findViewById(R.id.timerView1);

			timerView.setTime((long) road.getDuration());
			caloriesTextView.setText(road.getCalories() + " kcal");
			distanceTextView.setText(Math.round(road.getDistance() * 100)
					/ 100.d + " m");
		} catch (SQLException e1) {
			e1.printStackTrace();
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
