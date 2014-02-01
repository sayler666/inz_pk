package com.sayler.inz.agenda;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sayler.inz.R;
import com.sayler.inz.database.model.Agenda;
import com.sayler.inz.database.model.Road;

public class AgendaArrayAdapter extends ArrayAdapter<Agenda> {

	private Context context;

	public AgendaArrayAdapter(Context context, int textViewResourceId,
			List<Agenda> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.agenda_row, null);
		}

		Agenda item = getItem(position);
		if (item != null) {

			TextView dateView = (TextView) view
					.findViewById(R.id.date);
			Date date = item.getDateOfEvent();
			dateView.setText(date.toString());
			
			TextView agendaIdView = (TextView) view
					.findViewById(R.id.agenda_id);
			agendaIdView.setText(String.valueOf(item.getId()));

			
		}

		return view;
	}

}
