package ru.ming13.bustime.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.squareup.otto.Subscribe;

import ru.ming13.bustime.bus.BusProvider;
import ru.ming13.bustime.bus.StopSelectedEvent;
import ru.ming13.bustime.fragment.StopsMapFragment;
import ru.ming13.bustime.model.Stop;
import ru.ming13.bustime.provider.BusTimeContract;
import ru.ming13.bustime.util.Bartender;
import ru.ming13.bustime.util.Fragments;
import ru.ming13.bustime.util.Intents;

public class StopsMapActivity extends ActionBarActivity
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
		Fragments.Operator.set(this, buildFragment());
	}

	private Fragment buildFragment() {
		return StopsMapFragment.newInstance();
	}

	@Subscribe
	public void onStopSelected(StopSelectedEvent event) {
		startStopRoutesActivity(event.getStop());
	}

	private void startStopRoutesActivity(Stop stop) {
		Uri stopRoutesUri = BusTimeContract.Stops.buildStopsRoutesUri(stop.getId());

		Intent intent = Intents.Builder.with(this).buildStopRoutesIntent(stopRoutesUri, stop);
		startActivity(intent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case android.R.id.home:
				finish();
				return true;

			default:
				return super.onOptionsItemSelected(menuItem);
		}
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
