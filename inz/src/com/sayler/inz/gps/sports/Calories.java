package com.sayler.inz.gps.sports;

import android.content.SharedPreferences;

//different sports type - different calories calculation strategy
public class Calories {

	protected ISport _sport;

	public void setCaloriesCalculateStrategy(ISport sport) {
		this._sport = sport;
	}
	public ISport getCaloriesCalculateStrategy() {
		return this._sport;
	}
	public float calculate(float distance, int time, SharedPreferences sharedPref) {
		return _sport.calculateCalories(distance, time, sharedPref);
	}

}
