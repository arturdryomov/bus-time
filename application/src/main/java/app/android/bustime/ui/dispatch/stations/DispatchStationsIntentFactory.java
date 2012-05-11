package app.android.bustime.ui.dispatch.stations;


import android.content.Context;
import android.content.Intent;
import app.android.bustime.local.Station;
import app.android.bustime.ui.IntentFactory;


public class DispatchStationsIntentFactory extends IntentFactory
{
	public static Intent createStationsListIntent(Context context) {
		return new Intent(context, StationsListActivity.class);
	}

	public static Intent createStationCreationIntent(Context context) {
		return new Intent(context, StationCreationActivity.class);
	}

	public static Intent createRoutesListIntent(Context context, Station station) {
		Intent intent = new Intent(context, RoutesListActivity.class);
		intent.putExtra(MESSAGE_ID, station);

		return intent;
	}
}
