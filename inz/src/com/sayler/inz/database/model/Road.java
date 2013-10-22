package com.sayler.inz.database.model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.CloseableWrappedIterable;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Road extends AbstractObject {

	@DatabaseField()
	private double distance;
	@DatabaseField()
	private double duration;
	@DatabaseField()
	private double avg_speed;
	@DatabaseField()
	private int calories;
	@ForeignCollectionField
	private ForeignCollection<Track> tracks;
	
	

	public Road() {
		super();
		
	}

	public Road(double distance, double duration, double avg_speed, int calories) {
		super();
		this.distance = distance;
		this.duration = duration;
		this.avg_speed = avg_speed;
		this.calories = calories;
		this.createdAt = new Date();
	}

	public Road(double distance, double duration, double avg_speed,
			int calories, ForeignCollection<Track> tracks) {
		super();
		this.distance = distance;
		this.duration = duration;
		this.avg_speed = avg_speed;
		this.calories = calories;
		this.tracks = tracks;
		this.createdAt = new Date();
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public double getDuration() {
		return duration;
	}

	public void setDuration(double duration) {
		this.duration = duration;
	}

	public double getAvg_speed() {
		return avg_speed;
	}

	public void setAvg_speed(double avg_speed) {
		this.avg_speed = avg_speed;
	}

	public int getCalories() {
		return calories;
	}

	public void setCalories(int calories) {
		this.calories = calories;
	}

	public List<Track> getTracks() {
		ArrayList<Track> itemList = new ArrayList<Track>();
		for (Track item : tracks) {
			itemList.add(item);
		}
		return itemList;
	}

	public void setTracks(ForeignCollection<Track> tracks) {
		this.tracks = tracks;
	}
	

}
