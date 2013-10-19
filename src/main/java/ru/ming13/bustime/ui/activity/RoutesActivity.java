package ru.ming13.bustime.ui.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import ru.ming13.bustime.R;
import ru.ming13.bustime.db.model.Station;
import ru.ming13.bustime.ui.fragment.RoutesForStationFragment;
import ru.ming13.bustime.ui.intent.IntentException;
import ru.ming13.bustime.ui.intent.IntentExtras;
import ru.ming13.bustime.ui.loader.Loaders;
import ru.ming13.bustime.ui.loader.StationLoader;
import ru.ming13.bustime.ui.util.FragmentWrapper;


public class RoutesActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Station>
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		handleIntent(getIntent());
	}

	private void handleIntent(Intent intent) {
		if (intent.hasExtra(IntentExtras.STATION)) {
			setUpActivityWithReceivedStation();
			return;
		}

		if (intent.hasExtra(IntentExtras.STATION_ID)) {
			setUpActivityWithReceivedStationId();
			return;
		}

		throw new IntentException();
	}

	private void setUpActivityWithReceivedStation() {
		Station station = getIntent().getParcelableExtra(IntentExtras.STATION);

		setUpActionBarSubtitle(station.getName());

		setUpActivity(station);
	}

	private void setUpActionBarSubtitle(String stationName) {
		getSupportActionBar().setSubtitle(stationName);
	}

	private void setUpActivity(Station station) {
		FragmentWrapper.setUpFragment(this, RoutesForStationFragment.newInstance(station));
	}

	private void setUpActivityWithReceivedStationId() {
		setUpActivity();

		getSupportLoaderManager().initLoader(Loaders.STATION, getIntent().getExtras(), this);
	}

	private void setUpActivity() {
		FragmentWrapper.setUpFragment(this, RoutesForStationFragment.newInstance());
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

		setUpActionBarSubtitle(station.getName());

		routesForStationFragment.callListPopulation(station);

		getSupportLoaderManager().destroyLoader(Loaders.STATION);
	}

	@Override
	public void onLoaderReset(Loader<Station> stationLoader) {
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_action_bar_routes, menu);

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.menu_routes_order_name).setChecked(true);
		onOptionsItemSelected(menu.findItem(R.id.menu_routes_order_bus_time));

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		if (menuItem.isChecked()) {
			return false;
		}

		menuItem.setChecked(true);

		RoutesForStationFragment routesForStationFragment = (RoutesForStationFragment) getSupportFragmentManager().
			findFragmentById(android.R.id.content);

		switch (menuItem.getItemId()) {
			case R.id.menu_routes_order_name:
				routesForStationFragment.sortByName();
				return true;

			case R.id.menu_routes_order_bus_time:
				routesForStationFragment.sortByBusTime();
				return true;

			default:
				return super.onOptionsItemSelected(menuItem);
		}
	}
}
