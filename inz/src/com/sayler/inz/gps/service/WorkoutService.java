package com.sayler.inz.gps.service;

import de.greenrobot.event.EventBus;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class WorkoutService extends IntentService {
	final static String TAG = "WorkoutService";

	static boolean running = false;

	public static boolean isRunning(){
		return running;
	}
	
	public WorkoutService() {
		super(TAG);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onHandleIntent");
		try {
			running = true;
			while (true) {
				Thread.sleep(2000);
				EventBus.getDefault().post(new UpdateUiEvent());
			}
		} catch (Exception e) {
		}
	}

}
