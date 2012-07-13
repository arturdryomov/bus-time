package app.android.bustime.ui;


import android.content.Context;
import android.content.Intent;
import app.android.bustime.db.Route;
import app.android.bustime.db.Station;


public class IntentFactory
{
	public static final String MESSAGE_ID;
	public static final String EXTRA_MESSAGE_ID;

	static {
		MESSAGE_ID = String.format("%s.message", IntentFactory.class.getPackage().getName());
		EXTRA_MESSAGE_ID = String.format("%s.extramessage", IntentFactory.class.getPackage().getName());
	}

	public static Intent createTimetableIntent(Context context, Route route, Station station) {
		Intent intent = new Intent(context, TimetableActivity.class);
		intent.putExtra(MESSAGE_ID, route);
		intent.putExtra(EXTRA_MESSAGE_ID, station);

		return intent;
	}
}
