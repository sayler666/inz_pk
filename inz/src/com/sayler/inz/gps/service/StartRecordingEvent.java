package com.sayler.inz.gps.service;

import com.sayler.inz.database.model.Road;

public class StartRecordingEvent {
	public long currentRoadId;
	public Road currentRoad;

	public StartRecordingEvent(long currentRoadId, Road currentRoad) {
		this.currentRoadId = currentRoadId;
		this.currentRoad = currentRoad;
	}
}
