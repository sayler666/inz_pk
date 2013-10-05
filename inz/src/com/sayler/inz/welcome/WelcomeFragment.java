package com.sayler.inz.welcome;

import java.sql.SQLException;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.j256.ormlite.dao.ForeignCollection;
import com.sayler.inz.R;
import com.sayler.inz.data.RoadDataProvider;
import com.sayler.inz.data.TrackDataProvider;
import com.sayler.inz.database.DBSqliteOpenHelper;
import com.sayler.inz.database.DaoHelper;
import com.sayler.inz.database.model.Road;
import com.sayler.inz.database.model.Track;

public class WelcomeFragment extends SherlockFragment {
		
	final String TAG = "WelcomeFragment";

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.welcome_fragment, container,
				false);

		
		DaoHelper.setOpenHelper(this.getActivity().getApplicationContext(),DBSqliteOpenHelper.class);
		
		TrackDataProvider trackData = new TrackDataProvider();
		RoadDataProvider roadData = new RoadDataProvider();
		
		Road r1 = new Road(1,2,3,4);
		
		roadData.save(r1);
		
//		Track t1 = new Track(1,2,3,4,5,r1);
//		Track t2 = new Track(1,2,3,4,5,r1);
//		Track t3 = new Track(1,2,3,4,5,r1);
//		
//		trackData.save(t1);
//		trackData.save(t2);
//		trackData.save(t3);
		
//		
//		try {
//			Road r1 = roadData.get(1);
//			Log.d(TAG, r1.toString());
//			List<Track> list = r1.getTracks();
//			
//			for (Track track : list) {
//				Log.d(TAG, track.toString());
//			}
//			
//			//update
//			r1.setCalories(666);
//			roadData.save(r1);
//			
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
		
		return view;
	}

}
