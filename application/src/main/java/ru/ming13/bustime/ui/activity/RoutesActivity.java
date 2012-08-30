package ru.ming13.bustime.ui.activity;


import android.support.v4.app.Fragment;
import ru.ming13.bustime.db.model.Station;
import ru.ming13.bustime.ui.fragment.RoutesFragment;
import ru.ming13.bustime.ui.intent.IntentException;
import ru.ming13.bustime.ui.intent.IntentExtras;


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
