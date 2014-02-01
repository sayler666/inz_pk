package com.sayler.inz.agenda;

import java.util.Calendar;

import com.sayler.inz.history.gpx.ChooseSportDialog.ChooseSportDialogListener;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TimePicker;

public class DatePickerFragment extends DialogFragment implements
		DatePickerDialog.OnDateSetListener {

	private static final String TAG = "DatePickerFragment";

	public interface OnDatePickerSetListener {
		void onDatePickerSet(int year, int month, int day);
	}

	OnDatePickerSetListener mListener;

	int timesCalled = 2;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		try {
			mListener = (OnDatePickerSetListener) getTargetFragment();
		} catch (ClassCastException e) {
			throw new ClassCastException(
					"Calling fragment must implement OnDateSetListener interface");
		}

	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), this, year, month, day);
	}

	public void onDateSet(DatePicker view, int year, int month, int day) {
		
		if (--timesCalled == 0) {
			mListener.onDatePickerSet(year, month, day);
		}
		
	}

}