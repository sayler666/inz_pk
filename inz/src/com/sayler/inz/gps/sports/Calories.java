package com.sayler.inz.gps.sports;



//different sports type - different calories calculation strategy
public class Calories{
	
	protected ISport _sport;
	
	public void setCaloriesCalculateStrategy(ISport sport){
		this._sport = sport;
	}
	
	public  float calculate(float distance, int weight, int height, int time){
		return _sport.calculateCalories(distance, weight, height, time);
	}
	
}
