package com.sayler.inz.gps;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.sayler.inz.R;

public class EndRecordingDialog extends DialogFragment {

	/*
	 * The activity that creates an instance of this dialog fragment must
	 * implement this interface in order to receive event callbacks. Each method
	 * passes the DialogFragment in case the host needs to query it.
	 */
	public interface EndRecordingDialogListener {
		public void onEndRecordingDialogPositiveClick();

	}

	// Use this instance of the interface to deliver action events
	EndRecordingDialogListener mListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		try {
			mListener = (EndRecordingDialogListener) getTargetFragment();
		} catch (ClassCastException e) {
			throw new ClassCastException(
					"Calling fragment must implement GpsNotFixedDialogListener interface");
		}

	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(R.string.end_recording_dialog_message)
				.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								mListener.onEndRecordingDialogPositiveClick();
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
