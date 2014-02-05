package ru.ming13.bustime.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.squareup.otto.Subscribe;

import ru.ming13.bustime.bus.BusProvider;
import ru.ming13.bustime.bus.RouteSelectedEvent;
import ru.ming13.bustime.fragment.StopRoutesFragment;
import ru.ming13.bustime.provider.BusTimeContract;
import ru.ming13.bustime.util.Fragments;
import ru.ming13.bustime.util.Intents;
import ru.ming13.bustime.util.TitleBuilder;

public class StopRoutesActivity extends ActionBarActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setUpSubtitle();
		setUpFragment();
	}

	private void setUpSubtitle() {
		getSupportActionBar().setSubtitle(buildSubtitle());
	}

	private String buildSubtitle() {
		String stopName = getStopName();
		String stopDirection = getStopDirection();

		return TitleBuilder.with(this).buildStopTitle(stopName, stopDirection);
	}

	private String getStopName() {
		return getIntent().getStringExtra(Intents.Extras.STOP_NAME);
	}

	private String getStopDirection() {
		return getIntent().getStringExtra(Intents.Extras.STOP_DIRECTION);
	}

	private void setUpFragment() {
		Fragments.Operator.set(this, buildFragment());
	}

	private Fragment buildFragment() {
		return StopRoutesFragment.newInstance(getRoutesUri());
	}

	private Uri getRoutesUri() {
		return getIntent().getParcelableExtra(Intents.Extras.URI);
	}

	@Subscribe
	public void onRouteSelected(RouteSelectedEvent event) {
		long routeId = event.getRouteId();
		String routeNumber = event.getRouteNumber();

		startTimetableActivity(routeId, routeNumber);
	}

	private void startTimetableActivity(long routeId, String routeNumber) {
		Uri timetableUri = BusTimeContract.Stops.buildStopTimetableUri(getRoutesUri(), routeId);
		String stopName = getStopName();
		String stopDirection = getStopDirection();

		Intent intent = Intents.Builder.with(this)
			.buildTimetableIntent(timetableUri, routeNumber, stopName, stopDirection);
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
