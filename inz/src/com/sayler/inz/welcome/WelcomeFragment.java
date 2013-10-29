package com.sayler.inz.welcome;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockFragment;
import com.sayler.inz.R;

public class WelcomeFragment extends SherlockFragment implements Runnable {

	final String TAG = "WelcomeFragment";
	private Button testButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.welcome_fragment, container,
				false);

		testButton = (Button) view.findViewById(R.id.test_button);

		// initializing and starting a new local Thread object
		Thread currentThread = new Thread(this);
		currentThread.start();

		return view;
	}

	@Override
	public void run() {
		try {
			Integer i = 10;
			while (i > 0) {
				// all the stuff we want our Thread to do goes here
				Thread.sleep(1000);
				// signaling things to the outside world goes like this
				Message msg = new Message();
				msg.arg1 = i;
				threadHandler.sendMessage(msg);
				i--;
			}

		} catch (InterruptedException e) {
			// don't forget to deal with the Exception !!!!!
		}
	}

	// Receives Thread's messages, interprets them and acts on the
	// current Activity as needed
	private Handler threadHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			// whenever the Thread notifies this handler we have
			// only this behavior

			testButton.setText("val " + msg.arg1);

		}
	};

}
