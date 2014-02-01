package com.sayler.inz.agenda;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.sayler.inz.R;
import com.sayler.inz.agenda.DatePickerFragment.OnDatePickerSetListener;
import com.sayler.inz.data.AgendaDataProvider;
import com.sayler.inz.data.RoadDataProvider;
import com.sayler.inz.database.DBSqliteOpenHelper;
import com.sayler.inz.database.DaoHelper;
import com.sayler.inz.database.model.Agenda;

public class AgendaFragment extends SherlockFragment implements
		OnItemClickListener, OnItemLongClickListener, OnDatePickerSetListener {
	private static String TAG = "AgendaFragment";

	private ListView listView;

	private AgendaDataProvider agendaDataProvider;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater
				.inflate(R.layout.agenda_fragment, container, false);

		setHasOptionsMenu(true);

		DaoHelper.setOpenHelper(getActivity().getApplicationContext(),
				DBSqliteOpenHelper.class);

		agendaDataProvider = new AgendaDataProvider();

		// listView
		listView = (ListView) view.findViewById(R.id.listView);

		// load cursor on separate thread
		loadCursor();

		// set long click on list items
		registerForContextMenu(listView);

		return view;
	}

	private void loadCursor() {
		try {
			AgendaArrayAdapter arrayAdapter = new AgendaArrayAdapter(
					getActivity().getApplicationContext(), R.layout.agenda_row,
					agendaDataProvider.getAll());
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

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.agenda_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.add_agenda:

			addAgenda();

			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void addAgenda() {
		DialogFragment dateDialog = new DatePickerFragment();
		dateDialog.setTargetFragment(this, 0);
		dateDialog
				.show(getActivity().getSupportFragmentManager(), "datePicker");
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		super.onCreateContextMenu(menu, v, menuInfo);

		android.view.MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.agenda_floating_context_menu, menu);

	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		Long agendaId = Long
				.valueOf(((TextView) ((LinearLayout) info.targetView)
						.findViewById(R.id.agenda_id)).getText().toString());

		switch (item.getItemId()) {

		case R.id.delete:
			 agendaDataProvider.delete(agendaId);
			 loadCursor();
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onDatePickerSet(int year, int month, int day) {

		try {
			Date date = new SimpleDateFormat("yyyy-MM-dd").parse(String
					.valueOf(year + "-" + month + "-" + day));
			Agenda newAgende = new Agenda(date);
			agendaDataProvider.save(newAgende);

			// refresh list
			loadCursor();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
