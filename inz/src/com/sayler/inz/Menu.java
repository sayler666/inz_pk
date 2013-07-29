package com.sayler.inz;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class Menu extends ListFragment {

	static String TAG="Menu";
	
	private MenuListAdapter mListAdapter;
	private String[] menu_sections, menu_icons, menu_fragments;
	private ArrayList<Fragment> initializedFragments;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.menu_layout, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		menu_sections = getResources().getStringArray(R.array.menu_sections);
		menu_fragments = getResources().getStringArray(R.array.menu_fragments);
		menu_icons = getResources().getStringArray(R.array.menu_icons);

		mListAdapter = new MenuListAdapter(getActivity(), menu_sections,
				menu_sections, menu_icons);

		setListAdapter(mListAdapter);
		
		initializedFragments = new ArrayList<Fragment>();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		
		Class<?> fragmentClass;
		Fragment newFragment = null;
		boolean exists = false;
		try {
			//class name of chosen fragment
			fragmentClass = Class.forName(menu_fragments[position]);
			
			//if chosen fragment instance already exists in array of initialized fragment
			for (Fragment f : initializedFragments) {
				if(f.getClass() == fragmentClass){
					Log.d(TAG,"instantion already exists. "+f.getClass().toString()+ " "+fragmentClass.toString());
					exists = true;
					newFragment = f;
					Log.d(TAG,menu_fragments[position]+" - old instantion");
					break;
				}
			}
			//if fragment instance not exists - creating new instance
			if(!exists){
				newFragment = (Fragment) fragmentClass.newInstance();
				initializedFragments.add(newFragment);
				Log.d(TAG,menu_fragments[position]+" - new instantion");
			}
			
			//replace old fragment with new
			if(getActivity() instanceof FragmentSwitchable){
				FragmentSwitchable fragmentSwitcher = (FragmentSwitchable) getActivity();
				fragmentSwitcher.switchFragment(newFragment,exists, menu_sections[position]);
			}else{
				Log.d(TAG,"Activity must be instance of FragmentSwitchable!");  
			}
			
		} catch (ClassNotFoundException e) {
			Log.d(TAG,menu_fragments[position]+" - class not found");
			e.printStackTrace();
		} catch (java.lang.InstantiationException e) {
			Log.d(TAG,menu_fragments[position]+" - is not a Fragment");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			
			e.printStackTrace();
		}
		
		
		
		super.onListItemClick(l, v, position, id);
	}

	/**
	 * Activity must implements this interface to change fragments
	 * 
	 * @author sayler
	 */
	public interface FragmentSwitchable {
		/**
		 * @param f
		 *            fragment chosen from menu, to be shown in activity
		 */
		void switchFragment(Fragment f, boolean exists, String title);
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
			int iconDraw = this.context.getResources().getIdentifier(
					mIcon[position], "drawable", this.context.getPackageName());
			imgIcon.setImageResource(iconDraw);

			return itemView;
		}

	}

}
