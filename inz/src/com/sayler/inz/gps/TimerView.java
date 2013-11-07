package com.sayler.inz.gps;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

public class TimerView extends TextView {

	private static final String TAG = "TimerView";
	private long startTime;
	private Timer timer = null;
	/**
	 * seconds
	 */
	private int elapsedTime;

	public TimerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.clear();

	}

	final Handler h = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			elapsedTime = (int) ((System.currentTimeMillis() - startTime) / 1000);
			setTime(elapsedTime);
			return false;
		}
	});

	class TimeCounter extends TimerTask {

		@Override
		public void run() {
			h.sendEmptyMessage(0);
		}
	};

	public void start() {
		//Log.d(this.getClass().toString(), "start");
		timer = new Timer();
		startTime = System.currentTimeMillis();
		timer.schedule(new TimeCounter(), 0, 500);
	}

	public void start(long time) {
		if (timer == null) {
			//Log.d(this.getClass().toString(), "start");
			timer = new Timer();
			startTime = time;
			timer.schedule(new TimeCounter(), 0, 500);
		}
	}

	public void end() {
		//Log.d(this.getClass().toString(), "stop");
		if (timer != null) {
			timer.cancel();
			timer = null;
		}

	}

	public void clear() {
		this.setText("00:00:00");
	}

	public void setTime(long seconds){
		int minutes = (int)seconds / 60;
		int hours = (int) (seconds / 3600);
		seconds = seconds % 60;
		minutes = minutes % 60;

		TimerView.this.setText(String.format("%02d:%02d:%02d", hours,
				minutes, seconds));
		
	}
	
	/**
	 * elapsed time in seconds
	 * 
	 * @return seconds
	 */
	public int getElapsedTime() {

		return elapsedTime;
	}

}
