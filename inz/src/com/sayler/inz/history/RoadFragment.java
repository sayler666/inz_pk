package com.sayler.inz.history;

import java.sql.SQLException;
import java.util.ArrayList;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
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

public class RoadFragment extends SherlockFragment {
	private GoogleMap map;
	private SupportMapFragment mapFragment;

	private static final String TAG = "RoadFragment";
	private long roadId;

	private TextView distanceTextView;
	private TextView caloriesTextView;
	private TimerView timerView;
	private RoadDataProvider roadDataProvider;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// get road id
		roadId = ((RoadActivity) getActivity()).getRoadId();
		
		View view = inflater.inflate(R.layout.road_fragment, container, false);

		// draw road on map
		final PolylineOptions roadLine = new PolylineOptions().width(5).color(
				Color.RED);

		// bounds - need to center map over road
		final LatLngBounds.Builder bc = new LatLngBounds.Builder();

		// ORM
		DaoHelper.setOpenHelper(getActivity(), DBSqliteOpenHelper.class);
		roadDataProvider = new RoadDataProvider();

		try {
			final Road road = roadDataProvider.get(roadId);
			final ArrayList<Track> tracks = (ArrayList<Track>) road.getTracks();

			for (Track t : tracks) {
				LatLng ll = new LatLng(t.getLat(), t.getLng());
				roadLine.add(ll);
				bc.include(ll);
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

			final Handler handler = new Handler();
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					map = mapFragment.getMap();

					// check if map is created
					if (map != null) {
						// road
						map.addPolyline(roadLine).setVisible(true);

						// finish marker
						if (tracks.size() > 0) {
							Track lastTrack = tracks.get(tracks.size() - 1);
							map.addMarker(new MarkerOptions()
									.position(
											new LatLng(lastTrack.getLat(),
													lastTrack.getLng()))
									.icon(BitmapDescriptorFactory
											.fromResource(R.drawable.marker_finish)));
						}
						// center and zoom map
						map.setOnCameraChangeListener(new OnCameraChangeListener() {
							@Override
							public void onCameraChange(CameraPosition position) {
								if (road.getTracks().size() > 0) {
									// Remove listener to prevent position reset
									// on camera
									// move
									map.setOnCameraChangeListener(null);
								}
							}
						});
						map.moveCamera(CameraUpdateFactory.newLatLngBounds(
								bc.build(), 50));

						handler.removeCallbacksAndMessages(null);
					} else {
						handler.postDelayed(this, 500);
					}
				}
			}, 500);

			// road info
			distanceTextView = (TextView) view
					.findViewById(R.id.distanceTextView);
			caloriesTextView = (TextView) view
					.findViewById(R.id.caloriesTextView);
			timerView = (TimerView) view.findViewById(R.id.timerView1);

			timerView.setTime((long) road.getDuration());
			caloriesTextView.setText(road.getCalories() + " kcal");
			distanceTextView.setText(Math.round(road.getDistance() * 100)
					/ 100.d + " m");
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		return view;
	}

}
