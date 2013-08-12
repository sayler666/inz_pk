package com.sayler.inz.history;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.sayler.inz.R;
import com.sayler.inz.gps.Database;

public class HistoryFragment extends SherlockFragment {

	private HistoryCursorAdapter customAdapter;
	private Database gpsDb;
	private ListView listView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.history_fragment, container,
				false);

		listView = (ListView) view.findViewById(R.id.listView);
		
		gpsDb = new Database(getActivity());
		
		new Handler().post(new Runnable() {
			@Override
			public void run() {
				customAdapter = new HistoryCursorAdapter(getActivity().getApplicationContext(),
						gpsDb.getAllRoads());
				listView.setAdapter(customAdapter);
			}
		});

		return view;
	}

}
