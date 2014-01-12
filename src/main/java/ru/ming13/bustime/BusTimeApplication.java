package ru.ming13.bustime;

import android.app.Application;

import com.bugsense.trace.BugSenseHandler;

public class BusTimeApplication extends Application
{
	@Override
	public void onCreate() {
		super.onCreate();

		setUpBugSense();
	}

	private void setUpBugSense() {
		if (isReleaseBuild()) {
			BugSenseHandler.initAndStartSession(this, getString(R.string.key_bugsense_project));
		}
	}

	private boolean isReleaseBuild() {
		return !BuildConfig.DEBUG;
	}
}
