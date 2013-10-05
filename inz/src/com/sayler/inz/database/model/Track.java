package com.sayler.inz.database.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Track extends AbstractObject {

	@DatabaseField()
	private double lat;
	@DatabaseField()
	private double lng;
	@DatabaseField()
	private double alt;
	@DatabaseField()
	private double speed;
	@DatabaseField()
	private long time;
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Road road;

	public Track() {
		super();

	}

	public Track(double lat, double lng, double alt, double speed, long time,
			Road road) {
		super();
		this.lat = lat;
		this.lng = lng;
		this.alt = alt;
		this.speed = speed;
		this.time = time;
		this.road = road;
	}

	public Track(double lat, double lng, double alt, double speed, long time) {
		super();
		this.lat = lat;
		this.lng = lng;
		this.alt = alt;
		this.speed = speed;
		this.time = time;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public double getAlt() {
		return alt;
	}

	public void setAlt(double alt) {
		this.alt = alt;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public double getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

}
