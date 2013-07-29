package com.sayler.inz;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.sayler.inz.Menu.FragmentSwitchable;

public class Launch extends SherlockFragmentActivity implements
		FragmentSwitchable {

	static String TAG = "Launch";

	private ActionBarDrawerToggle mDrawerToggle;
	private DrawerLayout mDrawerLayout;
	private FragmentManager fm;
	private Fragment activeFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launch);
		activeFragment = null;

		// menu fragment
		ListFragment menu = new com.sayler.inz.Menu();
		fm = getSupportFragmentManager();
		fm.beginTransaction().replace(R.id.left_drawer, menu).commit();

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		mDrawerLayout.setScrimColor(Color.GRAY);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {
			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
			
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				getSupportActionBar().setTitle("Menu");
			}
		};

		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		// HOME button
		// navidraver hide/show
		case android.R.id.home:

			if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
				mDrawerLayout.closeDrawer(Gravity.START);
			} else {
				mDrawerLayout.openDrawer(Gravity.START);
			}
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void switchFragment(Fragment f, boolean exists, String title) {
		Log.d(TAG, "switch fragment");

		if (exists) {
			if (activeFragment != null)
				fm.beginTransaction().hide(activeFragment).show(f).commit();
			else
				fm.beginTransaction().show(f).commit();

			activeFragment = f;
		} else {
			if (activeFragment != null)
				fm.beginTransaction().hide(activeFragment)
						.add(R.id.content_frame, f).commit();
			else
				fm.beginTransaction().add(R.id.content_frame, f).commit();
			activeFragment = f;
		}
		getSupportActionBar().setTitle(title);
		mDrawerLayout.closeDrawer(Gravity.START);
	}
}
