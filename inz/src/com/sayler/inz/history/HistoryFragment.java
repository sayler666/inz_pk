package com.sayler.inz.history;

import java.sql.SQLException;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.sayler.inz.R;
import com.sayler.inz.data.RoadDataProvider;
import com.sayler.inz.database.DBSqliteOpenHelper;
import com.sayler.inz.database.DaoHelper;
import com.sayler.inz.database.model.Road;

@SuppressLint("ShowToast")
public class HistoryFragment extends SherlockFragment implements
		OnItemClickListener {

	private ListView listView;

	static final int PICK_FILE_REQUEST = 1;

	private static String TAG = "HistoryFragment";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.history_fragment, container,
				false);

		setHasOptionsMenu(true);

		// listView
		listView = (ListView) view.findViewById(R.id.listView);

		// load cursor on separate thread
		new Handler().post(new Runnable() {
			@Override
			public void run() {

				// ORM
				DaoHelper.setOpenHelper(getActivity().getApplicationContext(),
						DBSqliteOpenHelper.class);
				RoadDataProvider roadData = new RoadDataProvider();

				try {
					List<Road> roads = roadData.getAll();
					HistoryArrayAdapter arrayAdapter = new HistoryArrayAdapter(
							getActivity().getApplicationContext(),
							R.layout.history_row, roadData.getAll());
					listView.setAdapter(arrayAdapter);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
		getActivity().overridePendingTransition(R.animator.left_to_right_show,
				R.animator.left_to_right_hide);

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		Log.d(TAG, "menu?");
		inflater.inflate(R.menu.history_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.import_gpx:
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("file/*");
			startActivityForResult(intent, PICK_FILE_REQUEST);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
		case PICK_FILE_REQUEST:
			if (resultCode == Activity.RESULT_OK){
				
				String path =  data.getData().getPath();
				Log.d(TAG, "selected file: " + path);
				
				//Import Road using GPX file
				ImportRoadToDB importer = new ImportRoadToDB(new ImportRoadFromGPX(path));
				Road roadToImport = importer.getRoad();
			}
			break;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
}
