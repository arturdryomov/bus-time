package ru.ming13.bustime.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.squareup.otto.Subscribe;

import ru.ming13.bustime.bus.BusProvider;
import ru.ming13.bustime.bus.StationSelectedEvent;
import ru.ming13.bustime.fragment.StationsMapFragment;
import ru.ming13.bustime.provider.BusTimeContract;
import ru.ming13.bustime.util.Bartender;
import ru.ming13.bustime.util.Fragments;
import ru.ming13.bustime.util.Intents;

public class StationsMapActivity extends ActionBarActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setUpBars();
		setUpFragment();
	}

	private void setUpBars() {
		Bartender.with(this).showBarsBackground(this);
	}

	private void setUpFragment() {
		Fragments.Operator.add(this, buildFragment());
	}

	private Fragment buildFragment() {
		return StationsMapFragment.newInstance();
	}

	@Subscribe
	public void onStationSelected(StationSelectedEvent event) {
		long stationId = event.getStationId();
		String stationName = event.getStationName();
		String stationDirection = event.getStationDirection();

		startStationRoutesActivity(stationId, stationName, stationDirection);
	}

	private void startStationRoutesActivity(long stationId, String stationName, String stationDirection) {
		Uri stationRoutesUri = BusTimeContract.Stations.buildStationRoutesUri(stationId);

		Intent intent = Intents.Builder.with(this)
			.buildStationRoutesIntent(stationRoutesUri, stationName, stationDirection);
		startActivity(intent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case android.R.id.home:
				callUpActivity();
				return true;

			default:
				return super.onOptionsItemSelected(menuItem);
		}
	}

	private void callUpActivity() {
		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();

		BusProvider.getBus().register(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		BusProvider.getBus().unregister(this);
	}
}
