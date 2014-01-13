package ru.ming13.bustime;

import android.annotation.TargetApi;
import android.app.Application;
import android.os.Build;
import android.os.StrictMode;

import com.bugsense.trace.BugSenseHandler;

public class BusTimeApplication extends Application
{
	@Override
	public void onCreate() {
		super.onCreate();

		setUpBugSense();

		setUpStrictMode();
	}

	private void setUpBugSense() {
		if (!isDebugBuild()) {
			BugSenseHandler.initAndStartSession(this, getString(R.string.key_bugsense_project));
		}
	}

	private boolean isDebugBuild() {
		return BuildConfig.DEBUG;
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	private void setUpStrictMode() {
		if (isDebugBuild()) {
			StrictMode.enableDefaults();
		}
	}
}
