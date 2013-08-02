package com.sayler.inz.gps.sports;

import android.util.Log;

public class Running implements ISport {

	@Override
	public float calculateCalories(float distance, int weight, int height,
			int time) {
		
		float mph = (float) (distance/1609.344) / (((float)time) / 3600);
		
		Log.d(this.getClass().toString(), distance+ " distance"+ (distance/1609.344)  + " ;  "+((float)time) + ": time " + (((float)time) / 3600)  );
		
		
		float lbs = (float) ((float)weight * 2.20462262) ;
		
		Log.d(this.getClass().toString(), weight +"; lbs "+ ((float)weight * 2.20462262));
		
		float calories = ((float) (0.046 * mph * lbs)
				+ (float) (0.066 * Math.pow(mph, 3)))*(((float)time)/3600) ;

		return calories;
	}

}
