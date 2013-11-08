package com.sayler.inz.test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;

import static org.robolectric.Robolectric.shadowOf_;
import android.app.Activity;
import android.view.View;
import android.widget.Button;

import com.sayler.inz.Launch;
import com.sayler.inz.R;



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
	public void testButtonVisibility() throws Exception {
		Button b = (Button) activity.findViewById(R.id.testButton);
		assertThat(b.getVisibility(), equalTo(View.VISIBLE));

	}
	
	@Test
	@Config(reportSdk = 10)
	public void testBackButtonCloseActivity() throws Exception {
		// Press the Back button tripple?
		activity.onBackPressed();
		activity.onBackPressed();
		activity.onBackPressed();

		ShadowActivity sa = Robolectric.shadowOf((Activity)activity);
		Thread.sleep(2000);
		// Check that the activity is finished
		assertTrue(sa.isFinishing());
	}
}