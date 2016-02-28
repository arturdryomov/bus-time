package ru.ming13.bustime;

import android.app.Application;
import android.os.StrictMode;

import ru.ming13.bustime.util.Android;

public final class BusTimeApplication extends Application
{
	@Override
	public void onCreate() {
		super.onCreate();

		setUpDetecting();
	}

	private void setUpDetecting() {
		if (Android.isDebugging()) {
			StrictMode.enableDefaults();
		}
	}
}
