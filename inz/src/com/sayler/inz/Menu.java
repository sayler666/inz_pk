package com.sayler.inz;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class Menu extends ListFragment {

	private MenuListAdapter mListAdapter;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.menu_layout, null);
	}
	
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		String[] menu_sections = getResources().getStringArray(
				R.array.menu_sections);
		String[] menu_fragments = getResources().getStringArray(
				R.array.menu_fragments);
		String[] menu_icons = getResources().getStringArray(R.array.menu_icons);

		
		mListAdapter = new MenuListAdapter(getActivity(), menu_sections, menu_sections, menu_icons);
		

		setListAdapter(mListAdapter);
	}

	// MENU LIST ADPATER

	private class MenuListAdapter extends BaseAdapter {
		final static String TAG = "MenuListAdapter";
		// Declare Variables
		Context context;
		String[] mTitle;
		String[] mSubTitle;
		String[] mIcon;
		LayoutInflater inflater;

		public MenuListAdapter(Context context, String[] title,
				String[] subtitle, String[] icon) {
			this.context = context;
			this.mTitle = title;
			this.mSubTitle = subtitle;
			this.mIcon = icon;
			
			Log.d(TAG, String.valueOf(this.mIcon.length));
		}

		@Override
		public int getCount() {
			return mTitle.length;
		}

		@Override
		public Object getItem(int position) {
			return mTitle[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			// Declare Variables
			TextView txtTitle;
			TextView txtSubTitle;
			ImageView imgIcon;

			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View itemView = inflater.inflate(R.layout.menu_row, parent, false);

			// Locate the TextViews in drawer_list_item.xml
			txtTitle = (TextView) itemView.findViewById(R.id.title);
			txtSubTitle = (TextView) itemView.findViewById(R.id.subtitle);

			// Locate the ImageView in drawer_list_item.xml
			imgIcon = (ImageView) itemView.findViewById(R.id.icon);

			// Set the results into TextViews
			txtTitle.setText(mTitle[position]);
			txtSubTitle.setText(mSubTitle[position]);
			
			Log.d(TAG, mIcon[position]);
			
			// Set the results into ImageView
			int iconDraw = this.context.getResources().getIdentifier(mIcon[position], "drawable", this.context.getPackageName());
			imgIcon.setImageResource(iconDraw);

			return itemView;
		}

	}

}
