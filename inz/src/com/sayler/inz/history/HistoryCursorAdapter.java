package com.sayler.inz.history;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sayler.inz.R;

public class HistoryCursorAdapter extends CursorAdapter{

	@SuppressWarnings("deprecation")
	public HistoryCursorAdapter(Context context, Cursor c) {
		super(context, c);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView textViewPersonName = (TextView) view.findViewById(R.id.title);
		long time = cursor.getLong(cursor.getColumnIndex(cursor.getColumnName(6)));
		CharSequence relativeTime = DateUtils
				.getRelativeTimeSpanString(time);
        textViewPersonName.setText(relativeTime);
 
        TextView textViewPersonPIN = (TextView) view.findViewById(R.id.subtitle);
        textViewPersonPIN.setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(3))));
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View retView = inflater.inflate(R.layout.history_row, parent, false);
 
        return retView;
	}

}
