package com.sayler.inz.history;

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
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.history_row, null);
		}

		Road item = getItem(position);
		if (item != null) {

			TextView textViewRelativeTime = (TextView) view
					.findViewById(R.id.title);
			long time = item.getCreatedAt().getTime();

			CharSequence relativeTime = DateUtils
					.getRelativeTimeSpanString(time);
			textViewRelativeTime.setText(relativeTime);

			TextView textViewSubtitle = (TextView) view
					.findViewById(R.id.subtitle);
			textViewSubtitle.setText(context.getResources().getText(
					R.string.distance)
					+ String.format(" %.2f m", item.getDistance()));

			TextView textViewCalories = (TextView) view
					.findViewById(R.id.calories);
			textViewCalories.setText(context.getResources().getText(
					R.string.calories)
					+ String.format(" %d kcal", item.getCalories()));

			TextView textViewRoadId = (TextView) view
					.findViewById(R.id.road_id);
			textViewRoadId.setText(String.valueOf(item.getId()));

			// icon
			// TODO: choose specific img type for each sport
			ImageView sportImage = (ImageView) view
					.findViewById(R.id.sport_type_img);
			Bitmap bmp = null;
			switch (item.getSport_type()) {
			case BICYCLING:
				bmp = BitmapFactory.decodeResource(context.getResources(),
						R.drawable.sport_type_bicycling);
				break;
			case RUNNING:
				bmp = BitmapFactory.decodeResource(context.getResources(),
						R.drawable.sport_type_running);
				break;
			default:
				bmp = BitmapFactory.decodeResource(context.getResources(),
						R.drawable.sport_type_running);

			}

			RoundedAvatarDrawable avatar = new RoundedAvatarDrawable(bmp);

			sportImage.setImageDrawable(avatar);

		}

		return view;
	}

}
