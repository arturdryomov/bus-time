package app.android.bustime.ui.activity;


import android.support.v4.app.Fragment;
import app.android.bustime.db.model.Route;
import app.android.bustime.ui.fragment.StationsFragment;
import app.android.bustime.ui.intent.IntentException;
import app.android.bustime.ui.intent.IntentExtras;


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
