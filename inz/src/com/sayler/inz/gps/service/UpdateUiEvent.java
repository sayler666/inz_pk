package com.sayler.inz.gps.service;

public class UpdateUiEvent {
	public float distance;
	public long time, currentRoadId;
	public boolean isGpsFixed;
	public boolean isRecording;
	public UpdateUiEvent(float distance, long time2,boolean isGpsFixed, boolean isRecording, long currentRoadId ) {
		this.distance = distance;
		this.time=time2;
		this.isGpsFixed=isGpsFixed;
		this.isRecording=isRecording;
		this.currentRoadId = currentRoadId;
	}
}
