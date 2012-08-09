package app.android.bustime.ui.activity;


import android.support.v4.app.Fragment;
import app.android.bustime.db.model.Station;
import app.android.bustime.ui.fragment.RoutesFragment;
import app.android.bustime.ui.intent.IntentException;
import app.android.bustime.ui.intent.IntentExtras;


public class RoutesActivity extends FragmentWrapperActivity
{
	@Override
	protected Fragment buildFragment() {
		return RoutesFragment.newInstance(extractReceivedStation());
	}

	private Station extractReceivedStation() {
		Station station = getIntent().getParcelableExtra(IntentExtras.STATION);

		if (station == null) {
			throw new IntentException();
		}

		return station;
	}
}
