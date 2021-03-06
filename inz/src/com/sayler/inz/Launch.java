package com.sayler.inz;

import java.util.Currency;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.sayler.inz.Menu.FragmentSwitchable;
import com.sayler.inz.gps.service.WorkoutService;
import com.sayler.inz.welcome.WelcomeFragment;

public class Launch extends SherlockFragmentActivity implements
		FragmentSwitchable, IlastIntent {

	static String TAG = "Launch";

	public static final String SWITCH_FRAGMENT_INTENT = "SWITCH_FRAGMENT_INTENT";

	private ActionBarDrawerToggle mDrawerToggle;
	private DrawerLayout mDrawerLayout;
	private FragmentManager fm;
	private Class<?> fragmentClass = null;
	private ListFragment menu;
	private WelcomeFragment welcomeF = new WelcomeFragment();
	// seconds
	private final int doubleBackClickTime = 3;
	Long lastBackClick=0l;
	private Bundle lastIntentExtras = null;
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {

		// delete old fragments
		if (fragmentClass != null) {
			Log.d(TAG, "onSaveInstanceState ");
			outState.putString("fragmentClass", fragmentClass.getName());
		}
		super.onSaveInstanceState(outState);
	}

	/**
	 * Menu class must implements this interface to restore fragment after
	 * saveInstanceState
	 * 
	 * @author 2
	 * 
	 */
	public interface RestorableFragment {
		/**
		 * Restore fragment of given class
		 * 
		 * @param fragmentClass
		 */
		void restoreFragment(Class<?> fragmentClass);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		fm = getSupportFragmentManager();

		setContentView(R.layout.activity_launch);

		// restore state
		if (savedInstanceState == null) {

			// welcome fragment
			fm.beginTransaction().replace(R.id.content_frame, welcomeF)
					.commit();

			// menu fragment
			menu = new com.sayler.inz.Menu();
			fm.beginTransaction().replace(R.id.left_drawer, menu).commit();
			
		} else {
			Log.d(TAG,
					"w bundle " + savedInstanceState.getString("fragmentClass"));
			try {
				fragmentClass = Class.forName(savedInstanceState
						.getString("fragmentClass"));
			} catch (ClassNotFoundException e1) {

				e1.printStackTrace();
			}

			// get menu fragment
			menu = (com.sayler.inz.Menu) fm.findFragmentById(R.id.left_drawer);

			// restore fragment if need
			RestorableFragment restorableF = (RestorableFragment) menu;
			restorableF.restoreFragment(fragmentClass);

		}

		// drawer layout
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerLayout.setScrimColor(0x88cccccc);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close);

		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		
		//auto show navigation drawer when start
		mDrawerLayout.openDrawer(GravityCompat.START);

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		// get intent and store it's data
		if (intent != null) {
			lastIntentExtras = intent.getExtras();

			//switch fragment
			if (intent.hasExtra(SWITCH_FRAGMENT_INTENT)) {
				try {
					((com.sayler.inz.Menu) menu).setFragmentByClassName(Class
							.forName(lastIntentExtras
									.getString(SWITCH_FRAGMENT_INTENT)));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// fragments can grab last intent
	public Bundle popLastIntentExtras() {
		try {
			return lastIntentExtras;
		} finally {
			lastIntentExtras=null;
		}
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
		// navigation drawer hide/show
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
	public void switchFragment(Fragment f, boolean exists, String title,
			Class<?> fragmentClass) {
		Log.d(TAG, "switch fragment");

		this.fragmentClass = fragmentClass;
		// replace fragments
		fm.beginTransaction().replace(R.id.content_frame, f).commit();
		// set title
		getSupportActionBar().setTitle(title);
		mDrawerLayout.closeDrawer(Gravity.START);

	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
	}
	
	@Override
	public void onBackPressed() {
		Long clickTime = System.currentTimeMillis();
		// back click twice to close app
		if (lastBackClick + (doubleBackClickTime*1000) > clickTime) {
			//stop WorkoutService
			//Intent workoutSe = new Intent(this, WorkoutService.class);
			//this.stopService(workoutSe);
			super.onBackPressed();
		} else {
			Toast.makeText(this, R.string.click_again_to_close,
					Toast.LENGTH_LONG).show();

			lastBackClick = clickTime;
		}
	}



}
