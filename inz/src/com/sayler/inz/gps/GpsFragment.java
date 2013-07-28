package com.sayler.inz.gps;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.sayler.inz.R;

public class GpsFragment extends Fragment implements OnClickListener {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.gps_fragment, container, false);

		Button button = (Button) view.findViewById(R.id.button1);
		button.setOnClickListener(this);

		return view;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.button1:

			Database db = new Database(getActivity());
			Tracks tr = new Tracks(10, 1, 13, 123);

			Toast.makeText(getActivity(),
					String.valueOf(db.addTrack(tr)), 10).show();
			break;

		}

	}

}
