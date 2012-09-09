package ru.ming13.bustime.ui.activity;


import android.support.v4.app.Fragment;
import ru.ming13.bustime.db.model.Station;
import ru.ming13.bustime.ui.fragment.RoutesForStationFragment;
import ru.ming13.bustime.ui.intent.IntentException;
import ru.ming13.bustime.ui.intent.IntentExtras;


public class RoutesActivity extends FragmentWrapperActivity
{
	@Override
	protected Fragment buildFragment() {
		return RoutesForStationFragment.newInstance(extractReceivedStation());
	}

	private Station extractReceivedStation() {
		Station station = getIntent().getParcelableExtra(IntentExtras.STATION);

		if (station == null) {
			throw new IntentException();
		}

		return station;
	}
}
