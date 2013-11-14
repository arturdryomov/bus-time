package ru.ming13.bustime;

import android.app.Application;

import com.bugsense.trace.BugSenseHandler;

public class BusTimeApplication extends Application
{
	@Override
	public void onCreate() {
		super.onCreate();

		if (isBugSenseEnabled()) {
			setUpBugSense();
		}
	}

	private boolean isBugSenseEnabled() {
		return !BuildConfig.DEBUG;
	}

	private void setUpBugSense() {
		BugSenseHandler.initAndStartSession(this, getString(R.string.key_bugsense_project));
	}
}
