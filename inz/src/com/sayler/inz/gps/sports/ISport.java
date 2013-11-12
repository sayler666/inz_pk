package com.sayler.inz.gps.sports;

import com.sayler.inz.database.model.SportTypes;

import android.content.Context;
import android.content.SharedPreferences;

public interface ISport {

	/**
	 * Return calculated calories count 
	 * @param distance in m
	 * @param time in seconds
	 * @return calories burned
	 */
	public float calculateCalories(float distance, int time, SharedPreferences sharedPref);
	public SportTypes getSportType();
}
