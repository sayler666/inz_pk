package com.sayler.inz.welcome;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.sayler.inz.R;

public class WelcomeFragment extends SherlockFragment {
	private SupportMapFragment map;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.welcome_fragment, container,
				false);

//		map = ((SupportMapFragment) getActivity().getSupportFragmentManager()
//				.findFragmentById(R.id.map)).getMap();

		GoogleMapOptions op = new GoogleMapOptions();

		op.camera(new CameraPosition(new LatLng(50.1243, 19.1243), 11, 0, 0));

		map = SupportMapFragment.newInstance(op);
		getActivity().getSupportFragmentManager().beginTransaction().add(R.id.map_content,(Fragment) map).commit();
		
		return view;
	}

}
