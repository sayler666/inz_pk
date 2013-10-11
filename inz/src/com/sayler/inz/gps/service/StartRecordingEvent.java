package com.sayler.inz.gps.service;

import com.sayler.inz.database.model.Road;

public class StartRecordingEvent {
	
	public Road currentRoad;

	public StartRecordingEvent( Road currentRoad) {

		this.currentRoad = currentRoad;
	}
}
