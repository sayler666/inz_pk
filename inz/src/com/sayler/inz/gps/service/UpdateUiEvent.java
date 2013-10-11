package com.sayler.inz.gps.service;

import com.sayler.inz.database.model.Road;

public class UpdateUiEvent {
	public float distance;
	public long time;
	public boolean isGpsFixed;
	public boolean isRecording;
	public double lat;
	public double lng;
	public float accuracy;
	public Road currentRoad;

	public UpdateUiEvent(float distance, long time, boolean isGpsFixed,
			boolean isRecording, double lat, double lng,
			float accuracy, Road currentRoad) {
		this.distance = distance;
		this.time = time;
		this.isGpsFixed = isGpsFixed;
		this.isRecording = isRecording;
		this.lat = lat;
		this.lng = lng;
		this.accuracy = accuracy;
		this.currentRoad = currentRoad;
	}
}
