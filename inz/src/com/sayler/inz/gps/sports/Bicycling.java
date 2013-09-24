package com.sayler.inz.gps.sports;

import android.content.SharedPreferences;
import android.util.Log;

public class Bicycling implements ISport {

	@Override
	public float calculateCalories(float distance, int time, SharedPreferences sharedPref) {

		int bikeWeight = Integer.parseInt(sharedPref.getString("bike_weight", "70"));
		int weight = Integer.parseInt(sharedPref.getString("weight", "70"));

		float mph = (float) (distance / 1609.344) / (((float) time) / 3600);

		float lbs = (float) ((float) (bikeWeight+weight) * 2.20462262);

		float calories = ((float) (0.046 * mph * lbs) + (float) (0.066 * Math
				.pow(mph, 3))) * (((float) time) / 3600);

		return calories;
	}

}
