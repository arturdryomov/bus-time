package app.android.bustime.ui;

import android.content.Context;
import android.content.Intent;
import app.android.bustime.local.Route;

public class IntentFactory
{
	public static final String MESSAGE_ID;

	static {
		MESSAGE_ID = String.format("%s.message", IntentFactory.class.getPackage().getName());
	}

	public static Intent createDeckCreationIntent(Context context) {
		Intent intent = new Intent(context, RouteCreationActivity.class);

		return intent;
	}

	public static Intent createDeckRenamingInten(Context context, Route route) {
		Intent intent = new Intent(context, RouteRenamingActivity.class);
		intent.putExtra(MESSAGE_ID, route);

		return intent;
	}

	public static Intent createDepartureTimesListIntent(Context context, Route route) {
		Intent intent = new Intent(context, DepartureTimesListActivity.class);
		intent.putExtra(MESSAGE_ID, route);

		return intent;
	}

	public static Intent createDepartureTimeCreationIntent(Context context, Route route) {
		Intent intent = new Intent(context, DepartureTimeCreationActivity.class);
		intent.putExtra(MESSAGE_ID, route);

		return intent;
	}
}
