package com.sayler.inz.history;

import java.sql.SQLException;
import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import com.sayler.inz.history.gpx.LoadingDialog;

public class RoadFragment extends SherlockFragment {
	private GoogleMap map;
	private SupportMapFragment mapFragment;

	private static final String TAG = "RoadFragment";
	private long roadId;

	private TextView distanceTextView;
	private TextView caloriesTextView;
	private TimerView timerView;
	private RoadDataProvider roadDataProvider;
	private View view;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			final Bundle savedInstanceState) {

		// get road id
		roadId = ((RoadActivity) getActivity()).getRoadId();

		view = inflater.inflate(R.layout.road_fragment, container, false);

		// map
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

		new CreateMap().execute();

		return view;
	}

	/*
	 * AsyncTask to generate map
	 */
	private class CreateMap extends AsyncTask<Intent, Integer, Long> {
		private LoadingDialog loadingDialog;
		private Road road;
		private PolylineOptions roadLine;
		private ArrayList<Track> tracks;
		private LatLngBounds.Builder bc;

		protected void onPreExecute() {
			// show dialog
			loadingDialog = new LoadingDialog();
			loadingDialog.setCancelable(false);
			loadingDialog.show(getFragmentManager(), "loading_dialog");
		}

		@Override
		protected Long doInBackground(Intent... data) {

			// draw road on map
			roadLine = new PolylineOptions().width(5).color(Color.RED);

			// bounds - need to center map over road
			bc = new LatLngBounds.Builder();

			// ORM
			DaoHelper.setOpenHelper(getActivity(), DBSqliteOpenHelper.class);
			roadDataProvider = new RoadDataProvider();

			try {
				road = roadDataProvider.get(roadId);
				tracks = (ArrayList<Track>) road.getTracks();

				for (Track t : tracks) {

					LatLng ll = new LatLng(t.getLat(), t.getLng());
					roadLine.add(ll);
					bc.include(ll);
				}

				map = mapFragment.getMap();

				// check if map is created
				while (map == null) {
					Log.d(TAG, "brak mapy");
					map = mapFragment.getMap();
					Thread.sleep(100);
				}

			} catch (SQLException e1) {
				e1.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(Long result) {

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

			if (tracks.size() > 0) {

				// map drawing
				// road
				map.addPolyline(roadLine).setVisible(true);

				// finish marker
				if (tracks.size() > 0) {
					Track lastTrack = tracks.get(tracks.size() - 1);
					map.addMarker(new MarkerOptions().position(
							new LatLng(lastTrack.getLat(), lastTrack.getLng()))
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
				map.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(),
						50));
			}
			// hide dialog
			loadingDialog.dismiss();

		}

	}

}
