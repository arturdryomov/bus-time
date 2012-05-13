package app.android.bustime.ui;


import android.content.Context;
import android.content.Intent;
import app.android.bustime.local.Route;
import app.android.bustime.local.Station;
import app.android.bustime.local.Time;


public class IntentFactory
{
	public static final String MESSAGE_ID;
	public static final String EXTRA_MESSAGE_ID;

	static {
		MESSAGE_ID = String.format("%s.message", IntentFactory.class.getPackage().getName());
		EXTRA_MESSAGE_ID = String.format("%s.extramessage", IntentFactory.class.getPackage().getName());
	}

	public static Intent createRouteCreationIntent(Context context) {
		return new Intent(context, RouteCreationActivity.class);
	}

	public static Intent createRouteRenamingIntent(Context context, Route route) {
		Intent intent = new Intent(context, RouteRenamingActivity.class);
		intent.putExtra(MESSAGE_ID, route);

		return intent;
	}

	public static Intent createDepartureTimetableIntent(Context context, Route route) {
		Intent intent = new Intent(context, DepartureTimetableActivity.class);
		intent.putExtra(MESSAGE_ID, route);

		return intent;
	}

	public static Intent createDepartureTimeCreationIntent(Context context, Route route) {
		Intent intent = new Intent(context, DepartureTimeCreationActivity.class);
		intent.putExtra(MESSAGE_ID, route);

		return intent;
	}

	public static Intent createDepartureTimeEditingIntent(Context context, Route route, Time time) {
		Intent intent = new Intent(context, DepartureTimeEditingActivity.class);
		intent.putExtra(MESSAGE_ID, route);
		intent.putExtra(EXTRA_MESSAGE_ID, time);

		return intent;
	}

	public static Intent createStationRenamingIntent(Context context, Station station) {
		Intent intent = new Intent(context, StationRenamingActivity.class);
		intent.putExtra(MESSAGE_ID, station);

		return intent;
	}

	public static Intent createShiftTimeEditingIntent(Context context, Route route, Station station) {
		Intent intent = new Intent(context, ShiftTimeEditingActivity.class);
		intent.putExtra(MESSAGE_ID, route);
		intent.putExtra(EXTRA_MESSAGE_ID, station);

		return intent;
	}

	public static Intent createTimetableIntent(Context context, Route route, Station station) {
		Intent intent = new Intent(context, TimetableActivity.class);
		intent.putExtra(MESSAGE_ID, route);
		intent.putExtra(EXTRA_MESSAGE_ID, station);

		return intent;
	}

	public static Intent createStationLocationIntent(Context context, double latitude,
		double longitude) {
		Intent intent = new Intent(context, StationLocationActivity.class);
		intent.putExtra(MESSAGE_ID, latitude);
		intent.putExtra(EXTRA_MESSAGE_ID, longitude);

		return intent;
	}

	public static Intent createSynchronizationIntent(Context context) {
		return new Intent(context, SynchronizationActivity.class);
	}
}
