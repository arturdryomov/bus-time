package ru.ming13.bustime.ui.intent;


import android.content.Context;
import android.content.Intent;
import ru.ming13.bustime.db.model.Route;
import ru.ming13.bustime.db.model.Station;
import ru.ming13.bustime.ui.activity.RoutesActivity;
import ru.ming13.bustime.ui.activity.StationsActivity;
import ru.ming13.bustime.ui.activity.StationsMapActivity;
import ru.ming13.bustime.ui.activity.TimetableActivity;


public final class IntentFactory
{
	private IntentFactory() {
	}

	public static Intent createRoutesIntent(Context context, Station station) {
		Intent intent = new Intent(context, RoutesActivity.class);
		intent.putExtra(IntentExtras.STATION, station);

		return intent;
	}

	public static Intent createStationsIntent(Context context, Route route) {
		Intent intent = new Intent(context, StationsActivity.class);
		intent.putExtra(IntentExtras.ROUTE, route);

		return intent;
	}

	public static Intent createStationsMapIntent(Context context) {
		return new Intent(context, StationsMapActivity.class);
	}

	public static Intent createTimetableIntent(Context context, Route route, Station station) {
		Intent intent = new Intent(context, TimetableActivity.class);
		intent.putExtra(IntentExtras.ROUTE, route);
		intent.putExtra(IntentExtras.STATION, station);

		return intent;
	}
}
