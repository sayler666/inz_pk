package com.sayler.inz.gps;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.sayler.inz.R;

import de.greenrobot.event.EventBus;

public class GpsNotFixedDialog extends DialogFragment {

	/*
	 * The activity that creates an instance of this dialog fragment must
	 * implement this interface in order to receive event callbacks. Each method
	 * passes the DialogFragment in case the host needs to query it.
	 */
	public interface GpsNotFixedDialogListener {
		public void onGpsNotFixedDialogPositiveClick();

	}

	// Use this instance of the interface to deliver action events
	GpsNotFixedDialogListener mListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		try {
			mListener = (GpsNotFixedDialogListener) getTargetFragment();
		} catch (ClassCastException e) {
			throw new ClassCastException(
					"Calling fragment must implement GpsNotFixedDialogListener interface");
		}

	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(R.string.gps_not_fixed_dialog_message)
				.setPositiveButton(R.string.ignore,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								mListener.onGpsNotFixedDialogPositiveClick();
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
}
