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
import android.widget.FrameLayout;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.sayler.inz.Menu.FragmentSwitchable;
import com.sayler.inz.welcome.WelcomeFragment;

public class Launch extends SherlockFragmentActivity implements
		FragmentSwitchable {

	static String TAG = "Launch";

	private ActionBarDrawerToggle mDrawerToggle;
	private DrawerLayout mDrawerLayout;
	private FragmentManager fm;
	private Fragment activeFragment = null;
	private Class<?> fragmentClass = null;
	private ListFragment menu;
	private WelcomeFragment welcomeF = new WelcomeFragment();

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
		activeFragment = null;

		// menu fragment
		menu = new com.sayler.inz.Menu();
		fm.beginTransaction().replace(R.id.left_drawer, menu).commit();

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

		// restore state
		if (savedInstanceState == null) {

			// welcome fragment
			fm.beginTransaction().replace(R.id.content_frame, welcomeF)
					.commit();
		} else {

			Log.d(TAG,
					"w bundle " + savedInstanceState.getString("fragmentClass"));

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
	public void switchFragment(Fragment f, boolean exists, String title,
			Class<?> fragmentClass) {
		Log.d(TAG, "switch fragment");

		this.fragmentClass = fragmentClass;
		this.activeFragment = f;
	
		
		fm.beginTransaction().replace(R.id.content_frame, f).commit();
		activeFragment = f;
		
		// if (welcomeF.isVisible()) {
		// fm.beginTransaction().remove(welcomeF).commit();
		// }

		// if (exists) {
		// if (activeFragment != null)
		// fm.beginTransaction().hide(activeFragment).show(f).commit();
		// else
		// fm.beginTransaction().show(f).commit();
		//
		// activeFragment = f;
		// } else {
		// if (activeFragment != null)
		// fm.beginTransaction().hide(activeFragment)
		// .add(R.id.content_frame, f).commit();
		// else
		// fm.beginTransaction().add(R.id.content_frame, f).commit();
		// activeFragment = f;
		// }
		getSupportActionBar().setTitle(title);
		mDrawerLayout.closeDrawer(Gravity.START);

	}
}
