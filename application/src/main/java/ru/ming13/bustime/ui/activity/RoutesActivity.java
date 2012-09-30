package ru.ming13.bustime.ui.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import ru.ming13.bustime.R;
import ru.ming13.bustime.db.model.Station;
import ru.ming13.bustime.ui.fragment.RoutesForStationFragment;
import ru.ming13.bustime.ui.intent.IntentException;
import ru.ming13.bustime.ui.intent.IntentExtras;
import ru.ming13.bustime.ui.util.FragmentWrapper;
import ru.ming13.bustime.ui.util.ListNavigationProvider;


public class RoutesActivity extends SherlockFragmentActivity implements ActionBar.OnNavigationListener
{
	private static final class ListNavigationIndexes
	{
		private ListNavigationIndexes() {
		}

		public static final int SORTING_BY_BUS_TIME = 0;
		public static final int SORTING_BY_NAME = 1;
	}

	private ListNavigationProvider listNavigationProvider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FragmentWrapper.setUpFragment(this, buildFragment());

		setUpListNavigation(savedInstanceState);
	}

	private Fragment buildFragment() {
		return RoutesForStationFragment.newInstance(extractReceivedStation());
	}

	private Station extractReceivedStation() {
		Station station = getIntent().getParcelableExtra(IntentExtras.STATION);

		if (station == null) {
			throw new IntentException();
		}

		return station;
	}

	private void setUpListNavigation(Bundle activityInState) {
		listNavigationProvider = new ListNavigationProvider(this);

		listNavigationProvider.setUpListNavigation(this, R.array.titles_routes_with_sorting);

		listNavigationProvider.restoreSelectedNavigationIndex(activityInState);
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		RoutesForStationFragment routesForStationFragment = (RoutesForStationFragment) getSupportFragmentManager().
			findFragmentById(android.R.id.content);

		switch (itemPosition) {
			case ListNavigationIndexes.SORTING_BY_BUS_TIME:
				routesForStationFragment.sortByBusTime();
				return true;

			case ListNavigationIndexes.SORTING_BY_NAME:
				routesForStationFragment.sortByName();
				return true;

			default:
				return false;
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		listNavigationProvider.saveSelectedNavigationIndex(outState);
	}
}
