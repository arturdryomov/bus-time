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
import ru.ming13.bustime.fragment.RouteStopsFragment;
import ru.ming13.bustime.provider.BusTimeContract;
import ru.ming13.bustime.util.Fragments;
import ru.ming13.bustime.util.Intents;
import ru.ming13.bustime.util.TitleBuilder;

public class RouteStopsActivity extends ActionBarActivity
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
		String routeNumber = getRouteNumber();
		String routeDescription = getRouteDescription();

		return TitleBuilder.with(this).buildRouteTitle(routeNumber, routeDescription);
	}

	private String getRouteNumber() {
		return getIntent().getStringExtra(Intents.Extras.ROUTE_NUMBER);
	}

	private String getRouteDescription() {
		return getIntent().getStringExtra(Intents.Extras.ROUTE_DESCRIPTION);
	}

	private void setUpFragment() {
		Fragments.Operator.set(this, buildFragment());
	}

	private Fragment buildFragment() {
		return RouteStopsFragment.newInstance(getStopsUri());
	}

	private Uri getStopsUri() {
		return getIntent().getParcelableExtra(Intents.Extras.URI);
	}

	@Subscribe
	public void onStopSelected(StopSelectedEvent event) {
		long stopId = event.getStopId();
		String stopName = event.getStopName();
		String stopDirection = event.getStopDirection();

		startTimetableActivity(stopId, stopName, stopDirection);
	}

	private void startTimetableActivity(long stopId, String stopName, String stopDirection) {
		Uri timetableUri = BusTimeContract.Routes.buildRouteTimetableUri(getStopsUri(), stopId);
		String routeNumber = getRouteNumber();

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
