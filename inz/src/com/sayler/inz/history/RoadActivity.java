package com.sayler.inz.history;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sayler.inz.R;
import com.sayler.inz.data.RoadDataProvider;
import com.sayler.inz.database.DBSqliteOpenHelper;
import com.sayler.inz.database.DaoHelper;
import com.sayler.inz.database.model.Road;
import com.sayler.inz.database.model.Track;
import com.sayler.inz.gps.TimerView;
import com.sayler.inz.history.gpx.ExportRoadToGPX;



public class RoadActivity extends SherlockFragmentActivity {
	private GoogleMap map;

	private static final String TAG = "RoadActivity";
	private long roadId;

	private TextView distanceTextView;
	private TextView caloriesTextView;
	private TimerView timerView;
	private RoadDataProvider roadDataProvider;
	
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
		roadDataProvider = new RoadDataProvider();

		try {
			final Road road = roadDataProvider.get(roadId);
			ArrayList<Track> tracks = (ArrayList<Track>) road.getTracks();

			for (Track t : tracks) {
				LatLng ll = new LatLng(t.getLat(), t.getLng());
				roadLine.add(ll);
				bc.include(ll);
			}

			// maps stuff
			map = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			map.addPolyline(roadLine);

			// finish marker
			if (tracks.size() > 0) {
				Track lastTrack = tracks.get(tracks.size() - 1);
				map.addMarker(new MarkerOptions().position(
						new LatLng(lastTrack.getLat(), lastTrack.getLng()))
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.marker_finish)));
			}

			map.setOnCameraChangeListener(new OnCameraChangeListener() {
				@Override
				public void onCameraChange(CameraPosition position) {
					if (road.getTracks().size() > 0) {
						// Move camera
						map.moveCamera(CameraUpdateFactory.newLatLngBounds(
								bc.build(), 50));
						// Remove listener to prevent position reset on camera
						// move
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
	public boolean onCreateOptionsMenu(Menu menu) {
		com.actionbarsherlock.view.MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.road_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.onBackPressed();
			return true;
		case R.id.export_gpx:
			exportGpx(roadId);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void exportGpx(Long roadId) {

		try {
			// get road object
			Road roadToExport = roadDataProvider.get(roadId);

			// export to GPX file
			String filePath = ExportRoadToGPX.export(roadToExport);

			Toast.makeText(this, "File saved to: " + filePath,
					Toast.LENGTH_SHORT).show();
		} catch (SQLException e) {
			Toast.makeText(this, "Error reading road data!" + roadId,
					Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} catch (ParserConfigurationException es) {
			Toast.makeText(this, "Error reading road data!" + roadId,
					Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			Toast.makeText(this, "Error writing to sdcard!" + roadId,
					Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.animator.right_to_left_show,
				R.animator.right_to_left_hide);
	}

}
