package com.sayler.inz.gps.sports;

public interface ISport {
	/**
	 * Return calculated calories count 
	 * @param distance in m
	 * @param weight in kg
	 * @param height in cm
	 * @param time in seconds
	 * @return calories burned
	 */
	public float calculateCalories(float distance, int weight, int height, int time);
}
