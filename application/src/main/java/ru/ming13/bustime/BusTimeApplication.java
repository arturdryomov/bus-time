package ru.ming13.bustime;


import android.app.Application;
import com.bugsense.trace.BugSenseHandler;
import ru.ming13.bustime.db.DbProvider;
import ru.ming13.bustime.ui.bus.BusEventsCollector;
import ru.ming13.bustime.ui.bus.BusProvider;


public class BusTimeApplication extends Application
{
	@Override
	public void onCreate() {
		super.onCreate();

		setUpDatabase();
		setUpBusEventsCollector();
		setUpBugsense();
	}

	private void setUpDatabase() {
		DbProvider.setUp(this);
	}

	private void setUpBusEventsCollector() {
		BusProvider.getInstance().register(BusEventsCollector.getInstance());
	}

	private void setUpBugsense() {
		if (isBugsenseEnabled()) {
			BugSenseHandler.initAndStartSession(this, getBugsenseProjectKey());
		}
	}

	private boolean isBugsenseEnabled() {
		return getResources().getBoolean(R.bool.flag_bugsense_enabled);
	}

	private String getBugsenseProjectKey() {
		return getString(R.string.key_bugsense_project);
	}
}
