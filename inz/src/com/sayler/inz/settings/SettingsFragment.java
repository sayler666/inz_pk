package com.sayler.inz.settings;

import android.os.Bundle;

import com.sayler.inz.R;

public class SettingsFragment extends android.support.v4.preference.PreferenceFragment {

	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);

	}

}
