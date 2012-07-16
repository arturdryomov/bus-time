package app.android.bustime.ui;


import android.content.Context;
import android.content.Intent;
import app.android.bustime.db.Route;
import app.android.bustime.db.Station;


final class IntentFactory
{
	private static final String MESSAGE_ID;
	private static final String EXTRA_MESSAGE_ID;

	static {
		MESSAGE_ID = String.format("%s.message", IntentFactory.class.getPackage().getName());
		EXTRA_MESSAGE_ID = String.format("%s.extramessage", IntentFactory.class.getPackage().getName());
	}

	private IntentFactory() {
	}

	public static String getMessageId() {
		return MESSAGE_ID;
	}

	public static String getExtraMessageId() {
		return EXTRA_MESSAGE_ID;
	}

	public static Intent createRoutesIntent(Context context, Station station) {
		Intent intent = new Intent(context, RoutesActivity.class);
		intent.putExtra(MESSAGE_ID, station);

		return intent;
	}

	public static Intent createStationsIntent(Context context, Route route) {
		Intent intent = new Intent(context, StationsActivity.class);
		intent.putExtra(MESSAGE_ID, route);

		return intent;
	}

	public static Intent createStationsMapIntent(Context context) {
		return new Intent(context, StationsMapActivity.class);
	}

	public static Intent createTimetableIntent(Context context, Route route, Station station) {
		Intent intent = new Intent(context, TimetableActivity.class);
		intent.putExtra(MESSAGE_ID, route);
		intent.putExtra(EXTRA_MESSAGE_ID, station);

		return intent;
	}
}
