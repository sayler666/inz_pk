package com.sayler.inz.history;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.sayler.inz.R;

public class RoadActivity extends SherlockFragmentActivity {
	private SupportMapFragment map;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		//set layout
		setContentView(R.layout.road_activity);

		//maps stuff
		GoogleMapOptions op = new GoogleMapOptions();
		op.camera(new CameraPosition(new LatLng(50.1243, 19.1243), 11, 0, 0));
		map = SupportMapFragment.newInstance(op);
		getSupportFragmentManager().beginTransaction()
				.add(R.id.map_content, (Fragment) map).commit();

		//action bar 
		ActionBar ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
        case android.R.id.home:
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
	}
	
	

}
