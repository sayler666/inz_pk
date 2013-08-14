package com.sayler.inz.history;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.sayler.inz.R;
import com.sayler.inz.gps.Database;

@SuppressLint("ShowToast")
public class HistoryFragment extends SherlockFragment implements
		OnItemClickListener {

	private HistoryCursorAdapter customAdapter;
	private Database gpsDb;
	private ListView listView;

	private static String TAG = "HistoryFragment"; 
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.history_fragment, container,
				false);

		// database
		gpsDb = new Database(getActivity());
		
		// listView
		listView = (ListView) view.findViewById(R.id.listView);

		// load cursor on separate thread
		new Handler().post(new Runnable() {
			@Override
			public void run() {
				customAdapter = new HistoryCursorAdapter(getActivity()
						.getApplicationContext(), gpsDb.getAllRoads());
				listView.setAdapter(customAdapter);
			}
		});

		// set listener on list items
		listView.setOnItemClickListener(this);

		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// extract RoadId from invisible textview
		long roadId = Long.valueOf(((TextView) arg1.findViewById(R.id.road_id))
				.getText().toString());

		// start Road activity
		Intent roadActivityIntent = new Intent(getActivity(),
				RoadActivity.class);
		roadActivityIntent.putExtra("roadId", roadId);
		startActivity(roadActivityIntent);
		getActivity().overridePendingTransition(R.animator.left_to_right_show, R.animator.left_to_right_hide);

	}

}
