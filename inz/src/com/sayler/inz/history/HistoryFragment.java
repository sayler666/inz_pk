package com.sayler.inz.history;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.sayler.inz.R;
import com.sayler.inz.data.RoadDataProvider;
import com.sayler.inz.data.TrackDataProvider;
import com.sayler.inz.database.DBSqliteOpenHelper;
import com.sayler.inz.database.DaoHelper;
import com.sayler.inz.database.model.Road;
import com.sayler.inz.database.model.SportTypes;
import com.sayler.inz.database.model.Track;
import com.sayler.inz.gps.sports.Calories;
import com.sayler.inz.gps.sports.ISport;
import com.sayler.inz.history.gpx.ChooseSportDialog;
import com.sayler.inz.history.gpx.ExportRoadToGPX;
import com.sayler.inz.history.gpx.ImportRoadFromGPX;
import com.sayler.inz.history.gpx.ImportRoadToDB;
import com.sayler.inz.history.gpx.LoadingDialog;
import com.sayler.inz.history.gpx.ChooseSportDialog.ChooseSportDialogListener;

@SuppressLint("ShowToast")
public class HistoryFragment extends SherlockFragment implements
		OnItemClickListener, OnItemLongClickListener, ChooseSportDialogListener {

	private ListView listView;
	private RoadDataProvider roadDataProvider;
	static final int PICK_FILE_REQUEST = 101;

	private Calories caloriesCalculation = new Calories();
	private static String TAG = "HistoryFragment";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.history_fragment, container,
				false);

		setHasOptionsMenu(true);

		// ORM
		DaoHelper.setOpenHelper(getActivity().getApplicationContext(),
				DBSqliteOpenHelper.class);

		// road provider
		roadDataProvider = new RoadDataProvider();

		// listView
		listView = (ListView) view.findViewById(R.id.listView);

		// load cursor on separate thread
		loadCursor();

		// set listener on list items
		listView.setOnItemClickListener(this);

		// set long click on list items
		registerForContextMenu(listView);

		return view;
	}

	private void loadCursor() {

		// TODO: separete thread
		try {
			HistoryArrayAdapter arrayAdapter = new HistoryArrayAdapter(
					getActivity().getApplicationContext(),
					R.layout.history_row, roadDataProvider.getAll());
			listView.setAdapter(arrayAdapter);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// extract RoadId from invisible TextView
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
		inflater.inflate(R.menu.history_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.import_gpx:
			
			importGpx();

			break;
		}
		return super.onOptionsItemSelected(item);
	}

	

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		super.onCreateContextMenu(menu, v, menuInfo);

		// menu to export/delete road
		android.view.MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.history_floating_context_menu, menu);

	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		Long roadId = Long.valueOf(((TextView) ((LinearLayout) info.targetView)
				.findViewById(R.id.road_id)).getText().toString());

		switch (item.getItemId()) {

		case R.id.delete:
			// delete road
			roadDataProvider.delete(roadId);
			// refresh list
			loadCursor();
			return true;
		case R.id.export_gpx:
			
			exportGpx(roadId);
			
			return true;
		default:
			return super.onContextItemSelected(item);
		}

	}

	private void exportGpx(Long roadId){
		
		try {
			//get road object
			Road roadToExport = roadDataProvider.get(roadId);
			
			//export to GPX file
			String filePath = ExportRoadToGPX.export(roadToExport);
			
			Toast.makeText(getActivity(),
					"File saved to: "+filePath, Toast.LENGTH_SHORT)
					.show();
		} catch (SQLException e) {
			Toast.makeText(getActivity(),
					"Error reading road data!"+roadId, Toast.LENGTH_SHORT)
					.show();
			e.printStackTrace();
		} catch (ParserConfigurationException es){
			Toast.makeText(getActivity(),
					"Error reading road data!"+roadId, Toast.LENGTH_SHORT)
					.show();
		} catch (IOException e) {
			Toast.makeText(getActivity(),
					"Error writing to sdcard!"+roadId, Toast.LENGTH_SHORT)
					.show();
			e.printStackTrace();
		}
		
	}
	
	private void importGpx() {

		// dialog to choose sport
		ChooseSportDialog gpsTurnOnDialog = new ChooseSportDialog();
		gpsTurnOnDialog.setTargetFragment(this, 0);
		gpsTurnOnDialog.show(getFragmentManager(), "turn_on_gps");

	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Log.d(TAG, "onActivityResult, rc: " + resultCode + ", request: "
		// + requestCode);
		switch (requestCode) {
		// on result of picking file to import
		case PICK_FILE_REQUEST:
			if (resultCode == Activity.RESULT_OK) {

				// long time task
				new ImportRoadTask().execute(data);
			}
			break;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onChooseSportDialogPositiveClick(ISport sport) {
		caloriesCalculation.setCaloriesCalculateStrategy(sport);

		// pick file to import
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("file/*");
		startActivityForResult(intent, PICK_FILE_REQUEST);
	}

	/*
	 * AsyncTask to import file
	 */
	private class ImportRoadTask extends AsyncTask<Intent, Integer, Long> {
		private LoadingDialog loadingDialog;

		protected void onPreExecute() {
			// show dialog
			loadingDialog = new LoadingDialog();
			loadingDialog.setCancelable(false);
			loadingDialog.show(getFragmentManager(), "loading_dialog");
		}

		@Override
		protected Long doInBackground(Intent... data) {

			String path = data[0].getData().getPath();
			Log.d(TAG, "selected file: " + path);

			// Import Road using GPX file
			ImportRoadToDB importer = new ImportRoadToDB(new ImportRoadFromGPX(
					path));

			try {
				// read file
				importer.read();
				// get tracks
				ArrayList<Track> roadTracks = (ArrayList<Track>) importer
						.getTracks();

				// set road data
				Road roadToImport = new Road();
				roadToImport.setCreatedAt(importer.getDate());
				roadToImport.setDistance(importer.getDistance());

				double duration = importer.getDuration();

				// calculate calories
				SharedPreferences sharedPref = PreferenceManager
						.getDefaultSharedPreferences(getActivity());

				float calories = caloriesCalculation.calculate(
						(float) roadToImport.getDistance(), (int) duration,
						sharedPref);
				SportTypes sportType = ((ISport) caloriesCalculation.getCaloriesCalculateStrategy()).getSportType();
				roadToImport.setSport_type(sportType);
				roadToImport.setDuration(duration);
				roadToImport.setCalories((int) calories);

				// save road to DB
				roadDataProvider.save(roadToImport);

				// save tracks
				TrackDataProvider trackDataProvider = new TrackDataProvider();
				for (Track track : roadTracks) {
					track.setRoad(roadToImport);
					trackDataProvider.save(track);
				}

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(Long result) {
			// hide dialog
			loadingDialog.dismiss();
			// refresh cursor
			loadCursor();
		}

	}

}
