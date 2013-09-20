package com.sayler.inz.gps.service;

public class UpdateUiEvent {
	public float distance;
	public long time, currentRoadId;
	public boolean isGpsFixed;
	public boolean isRecording;
	public double  lat;
	public double lng;
	public float accuracy;
	public UpdateUiEvent(float distance, long time,boolean isGpsFixed, boolean isRecording, long currentRoadId, double lat, double lng, float accuracy ) {
		this.distance = distance;
		this.time=time;
		this.isGpsFixed=isGpsFixed;
		this.isRecording=isRecording;
		this.currentRoadId = currentRoadId;
		this.lat = lat;
		this.lng = lng;
		this.accuracy = accuracy;
	}
}
