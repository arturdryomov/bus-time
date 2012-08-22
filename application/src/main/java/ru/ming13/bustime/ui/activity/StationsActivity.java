package ru.ming13.bustime.ui.activity;


import android.support.v4.app.Fragment;
import ru.ming13.bustime.db.model.Route;
import ru.ming13.bustime.ui.fragment.StationsFragment;
import ru.ming13.bustime.ui.intent.IntentException;
import ru.ming13.bustime.ui.intent.IntentExtras;


public class StationsActivity extends FragmentWrapperActivity
{
	@Override
	protected Fragment buildFragment() {
		return StationsFragment.newInstance(extractReceivedRoute());
	}

	private Route extractReceivedRoute() {
		Route route = getIntent().getParcelableExtra(IntentExtras.ROUTE);

		if (route == null) {
			throw new IntentException();
		}

		return route;
	}
}
