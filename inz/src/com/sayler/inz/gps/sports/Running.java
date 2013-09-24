package com.sayler.inz.gps.sports;

import android.content.SharedPreferences;

public class Running implements ISport {

	@Override
	public float calculateCalories(float distance, int time,
			SharedPreferences sharedPref) {

		int weight = Integer.parseInt(sharedPref.getString("weight", "70"));

		float mph = (float) (distance / 1609.344) / (((float) time) / 3600);

		float lbs = (float) ((float) weight * 2.20462262);

		float calories = (float) (0.75*lbs)*(float) (distance / 1609.344) ;

		return calories;
	}

}
