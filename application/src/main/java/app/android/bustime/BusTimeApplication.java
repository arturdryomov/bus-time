package app.android.bustime;


import android.app.Application;
import app.android.bustime.db.DbProvider;


public class BusTimeApplication extends Application
{
	@Override
	public void onCreate() {
		super.onCreate();

		DbProvider.getInstance(this);
	}
}
