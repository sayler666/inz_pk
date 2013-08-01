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

	private long startTime;
	private Timer timer;
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
			elapsedTime = (int)((System.currentTimeMillis() - startTime)/1000);
			
			long millis = System.currentTimeMillis() - startTime;

			int seconds = (int) (millis / 1000);
			int minutes = seconds / 60;
			int hours = (int) (millis / 3600000);
			seconds = seconds % 60;
			minutes = minutes % 60;
			TimerView.this.setText(String.format("%02d:%02d:%02d", hours,
					minutes, seconds));

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
		timer = new Timer();
		startTime = System.currentTimeMillis();
		timer.schedule(new TimeCounter(), 0, 500);
	}

	public void end() {
		timer.cancel();
		
	} 
	
	public void clear() {
		this.setText("00:00:00");
	}

	/**
	 * elapsed time in seconds
	 * @return seconds
	 */
	public int getElapsedTime(){
		
		return  elapsedTime;
	}
	
}
