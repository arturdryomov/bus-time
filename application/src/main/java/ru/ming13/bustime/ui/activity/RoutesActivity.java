package ru.ming13.bustime.ui.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import ru.ming13.bustime.R;
import ru.ming13.bustime.db.model.Station;
import ru.ming13.bustime.ui.fragment.RoutesForStationFragment;
import ru.ming13.bustime.ui.intent.IntentException;
import ru.ming13.bustime.ui.intent.IntentExtras;
import ru.ming13.bustime.ui.loader.Loaders;
import ru.ming13.bustime.ui.loader.StationLoader;
import ru.ming13.bustime.ui.util.FragmentWrapper;
import ru.ming13.bustime.ui.util.ListNavigationProvider;


public class RoutesActivity extends SherlockFragmentActivity implements ActionBar.OnNavigationListener, LoaderManager.LoaderCallbacks<Station>
{

	private static final class ListNavigationIndexes
	{
		private ListNavigationIndexes() {
		}

		public static final int SORTING_BY_BUS_TIME = 0;
		public static final int SORTING_BY_NAME = 1;
	}

	private Bundle savedInstanceState;

	private ListNavigationProvider listNavigationProvider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.savedInstanceState = savedInstanceState;

		handleIntent(getIntent());
	}

	private void handleIntent(Intent intent) {
		if (intent.hasExtra(IntentExtras.STATION)) {
			setUpActivityWithStation();
			return;
		}

		if (intent.hasExtra(IntentExtras.STATION_ID)) {
			setUpActivityWithStationId();
			return;
		}

		throw new IntentException();
	}

	private void setUpActivityWithStation() {
		Station station = getIntent().getParcelableExtra(IntentExtras.STATION);

		setUpActivity(station);
	}

	private void setUpActivity(Station station) {
		FragmentWrapper.setUpFragment(this, RoutesForStationFragment.newInstance(station));

		setUpListNavigation(savedInstanceState);
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

	private void setUpActivityWithStationId() {
		setUpActivity();

		getSupportLoaderManager().initLoader(Loaders.STATION, getIntent().getExtras(), this);
	}

	private void setUpActivity() {
		FragmentWrapper.setUpFragment(this, RoutesForStationFragment.newInstance());

		setUpListNavigation(savedInstanceState);
	}

	@Override
	public Loader<Station> onCreateLoader(int loaderId, Bundle loaderArguments) {
		long stationId = loaderArguments.getLong(IntentExtras.STATION_ID);

		return new StationLoader(this, stationId);
	}

	@Override
	public void onLoadFinished(Loader<Station> stationLoader, Station station) {
		RoutesForStationFragment routesForStationFragment = (RoutesForStationFragment) getSupportFragmentManager().findFragmentById(
			android.R.id.content);

		routesForStationFragment.callListPopulation(station);

		getSupportLoaderManager().destroyLoader(Loaders.STATION);
	}

	@Override
	public void onLoaderReset(Loader<Station> stationLoader) {
	}
}
