package com.sayler.inz.history;

import java.util.List;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sayler.inz.R;
import com.sayler.inz.database.model.Road;

public class HistoryArrayAdapter extends ArrayAdapter<Road> {

	private Context context;
	public HistoryArrayAdapter(Context context, int textViewResourceId,
			List<Road> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.history_row, null);
        }

        Road item = getItem(position);
        if (item!= null) {
           
    		TextView textViewRelativeTime = (TextView) view.findViewById(R.id.title);
    		long time = item.getCreatedAt().getTime();

    		CharSequence relativeTime = DateUtils.getRelativeTimeSpanString(time);
    		textViewRelativeTime.setText(relativeTime);
    
    		TextView textViewSubtitle = (TextView) view
    				.findViewById(R.id.subtitle);
    		textViewSubtitle.setText(context.getResources().getText(R.string.distance)+String.format(" %.2f m", item.getDistance()));
    		
    		TextView textViewCalories = (TextView) view
    				.findViewById(R.id.calories);
    		textViewCalories.setText(context.getResources().getText(R.string.calories)+String.format(" %d kcal", item.getCalories()));
    		
    		TextView textViewRoadId = (TextView) view
    				.findViewById(R.id.road_id);
    		textViewRoadId.setText(String.valueOf(item.getId()));
        	
         }

        return view;
    }




}
