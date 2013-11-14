package com.sayler.inz.history.gpx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.sayler.inz.R;
import com.sayler.inz.gps.sports.ISport;

public class ChooseSportDialog extends DialogFragment implements
		OnItemSelectedListener {

	private Spinner spinner;
	private String[] sportsList, sportsClasses;
	private String selectedSport = null;

	/*
	 * The activity that creates an instance of this dialog fragment must
	 * implement this interface in order to receive event callbacks. Each method
	 * passes the DialogFragment in case the host needs to query it.
	 */
	public interface ChooseSportDialogListener {
		public void onChooseSportDialogPositiveClick(ISport sportClass);

	}

	// Use this instance of the interface to deliver action events
	ChooseSportDialogListener mListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		try {
			mListener = (ChooseSportDialogListener) getTargetFragment();
		} catch (ClassCastException e) {
			throw new ClassCastException(
					"Calling fragment must implement ChooseSportDialogListener interface");
		}

	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.choose_sport_dialog, null);

		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		// filling spinner
		addItemsToSpinner(view);

		builder.setView(view);

		builder.setMessage(R.string.choose_sport_dialog_message)
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// get sport class
								Class<ISport> sportClass;
								ISport sport;
								try {
									sportClass = (Class<ISport>) Class
											.forName(selectedSport);
									sport = sportClass.newInstance();

									mListener
											.onChooseSportDialogPositiveClick(sport);


								} catch (java.lang.InstantiationException e) {
									e.printStackTrace();
								} catch (IllegalAccessException e) {
									e.printStackTrace();
								} catch (ClassNotFoundException e) {
									e.printStackTrace();
								}

							}
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

							}
						});

		// Create the AlertDialog object and return it
		return builder.create();
	}

	private void addItemsToSpinner(View view) {
		spinner = (Spinner) view.findViewById(R.id.spinner);

		sportsList = getResources().getStringArray(R.array.sports_list);
		sportsClasses = getResources().getStringArray(R.array.sports_classes);

		List<String> list = new ArrayList<String>(Arrays.asList(sportsList));

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
				getActivity(), android.R.layout.simple_spinner_item, list);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(dataAdapter);

		// add click event
		spinner.setOnItemSelectedListener(this);

	}

	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		// select sport class
		selectedSport = sportsClasses[pos];
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

}
