package com.sayler.inz.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;

import android.app.Activity;

import com.sayler.inz.Launch;



@RunWith(RobolectricTestRunner.class)
public class LaunchActivityTest {
	private Launch activity;

	@Before
	public void setUp() throws Exception {
		activity = Robolectric.buildActivity(Launch.class).create().get();
	}

	@Test
	@Config(reportSdk = 10)
	public void testActivity() throws Exception {
		assertNotNull(activity);
	}

	
	@Test
	@Config(reportSdk = 10)
	public void testBackButtonCloseActivity() throws Exception {

		activity.onBackPressed();
		activity.onBackPressed();

		//wait until app start finishing process
		ShadowActivity sa = Robolectric.shadowOf((Activity)activity);
		Thread.sleep(2000);
		// Check that the activity is finished
		assertTrue(sa.isFinishing());
	}
}