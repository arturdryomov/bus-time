package ru.ming13.bustime;


import android.app.Application;
import com.bugsense.trace.BugSenseHandler;
import ru.ming13.bustime.db.DbProvider;


public class BusTimeApplication extends Application
{
	@Override
	public void onCreate() {
		super.onCreate();

		setUpDatabase();
		setUpBugsense();
	}

	private void setUpDatabase() {
		DbProvider.getInstance(this);
	}

	private void setUpBugsense() {
		if (isBugsenseEnabled()) {
			BugSenseHandler.setup(this, getBugsenseProjectKey());
		}
	}

	private boolean isBugsenseEnabled() {
		return getResources().getBoolean(R.bool.flag_bugsense_enabled);
	}

	private String getBugsenseProjectKey() {
		return getString(R.string.key_bugsense_project);
	}
}
