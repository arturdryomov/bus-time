package app.android.bustime.ui;

import android.content.Context;
import android.content.Intent;

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
}
