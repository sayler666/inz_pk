package com.sayler.inz.history;

import java.io.IOException;
import java.sql.SQLException;

import javax.xml.parsers.ParserConfigurationException;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.maps.GoogleMap;
import com.sayler.inz.R;
import com.sayler.inz.data.RoadDataProvider;
import com.sayler.inz.database.DBSqliteOpenHelper;
import com.sayler.inz.database.DaoHelper;
import com.sayler.inz.database.model.Road;
import com.sayler.inz.gps.TimerView;
import com.sayler.inz.history.gpx.ExportRoadToGPX;
import com.sayler.inz.welcome.WelcomeFragment;

public class RoadActivity extends SherlockFragmentActivity {
	private GoogleMap map;

	private static final String TAG = "RoadActivity";
	private long roadId;

	private TextView distanceTextView;
	private TextView caloriesTextView;
	private TimerView timerView;

	private RoadDataProvider roadDataProvider;
	private ActionBar actionBar;
	RoadPagerAdapter roadCollectionPagerAdapter;
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		// set layout
		setContentView(R.layout.road_activity_tabs);

		// intent
		Intent intent = getIntent();
		roadId = intent.getLongExtra("roadId", 0);

		// action bar back
		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		// tabs
		initTabs();

		// ORM
		DaoHelper.setOpenHelper(this, DBSqliteOpenHelper.class);
		roadDataProvider = new RoadDataProvider();
	}

	private void initTabs() {
		// view pager
		mViewPager = (ViewPager) findViewById(R.id.pager);

		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.DKGRAY));
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {
			public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
				// when click tab choose corresponding page
				mViewPager.setCurrentItem(tab.getPosition());
			}

			public void onTabUnselected(ActionBar.Tab tab,
					FragmentTransaction ft) {
			}

			public void onTabReselected(ActionBar.Tab tab,
					FragmentTransaction ft) {
			}
		};

		// add tabs to action bar
		actionBar.addTab(actionBar.newTab().setText(R.string.road_tab)
				.setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setText(R.string.stats_tab)
				.setTabListener(tabListener));

		roadCollectionPagerAdapter = new RoadPagerAdapter(
				getSupportFragmentManager());

		mViewPager.setAdapter(roadCollectionPagerAdapter);
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						// When swiping between pages, select the
						// corresponding tab.
						getSupportActionBar().setSelectedNavigationItem(
								position);
					}

				});
	}

	public long getRoadId() {
		return roadId;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		com.actionbarsherlock.view.MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.road_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.onBackPressed();
			return true;
		case R.id.export_gpx:
			exportGpx(roadId);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void exportGpx(Long roadId) {

		try {
			// get road object
			Road roadToExport = roadDataProvider.get(roadId);

			// export to GPX file
			String filePath = ExportRoadToGPX.export(roadToExport);

			// file saved properly
			Toast.makeText(this, "File saved to: " + filePath,
					Toast.LENGTH_SHORT).show();
		} catch (SQLException e) {
			Toast.makeText(this, "Error reading road data!" + roadId,
					Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} catch (ParserConfigurationException es) {
			Toast.makeText(this, "Error reading road data!" + roadId,
					Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			Toast.makeText(this, "Error writing to sdcard!" + roadId,
					Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.animator.right_to_left_show,
				R.animator.right_to_left_hide);
	}

	/*
	 * page adapter with road and statistic fragments
	 */
	public class RoadPagerAdapter extends FragmentStatePagerAdapter {
		public RoadPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			Fragment fragment = null;
			switch (i) {
			case 0:
				fragment = new RoadFragment();
				break;

			case 1:
				fragment = new StatisticFragment();
				break;

			}
			return fragment;
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return "OBJECT " + (position + 1);
		}
	}

}
