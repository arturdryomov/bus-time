package app.android.bustime.ui.dispatch.routes;


import android.content.Context;
import android.content.Intent;
import app.android.bustime.local.Route;
import app.android.bustime.ui.IntentFactory;


public class DispatchRoutesIntentFactory extends IntentFactory
{
	public static Intent createRoutesListIntent(Context context) {
		return new Intent(context, RoutesListActivity.class);
	}

	public static Intent createStationCreationIntent(Context context, Route route) {
		Intent intent = new Intent(context, StationCreationActivity.class);
		intent.putExtra(MESSAGE_ID, route);

		return intent;
	}

	public static Intent createStationsListIntent(Context context, Route route) {
		Intent intent = new Intent(context, StationsListActivity.class);
		intent.putExtra(MESSAGE_ID, route);

		return intent;
	}
}