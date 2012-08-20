package ru.ming13.bustime;


import android.app.Application;
import ru.ming13.bustime.db.DbProvider;


public class BusTimeApplication extends Application
{
	@Override
	public void onCreate() {
		super.onCreate();

		setUpDatabase();
	}

	private void setUpDatabase() {
		DbProvider.getInstance(this);
	}
}
