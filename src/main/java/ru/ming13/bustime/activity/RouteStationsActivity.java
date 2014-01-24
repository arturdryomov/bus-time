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
import ru.ming13.bustime.fragment.RouteStationsFragment;
import ru.ming13.bustime.provider.BusTimeContract;
import ru.ming13.bustime.util.Fragments;
import ru.ming13.bustime.util.Intents;
import ru.ming13.bustime.util.TitleBuilder;

public class RouteStationsActivity extends ActionBarActivity
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
		return RouteStationsFragment.newInstance(getStationsUri());
	}

	private Uri getStationsUri() {
		return getIntent().getParcelableExtra(Intents.Extras.URI);
	}

	@Subscribe
	public void onStationSelected(StationSelectedEvent event) {
		long stationId = event.getStationId();
		String stationName = event.getStationName();
		String stationDirection = event.getStationDirection();

		startTimetableActivity(stationId, stationName, stationDirection);
	}

	private void startTimetableActivity(long stationId, String stationName, String stationDirection) {
		Uri timetableUri = BusTimeContract.Routes.buildRouteTimetableUri(getStationsUri(), stationId);
		String routeNumber = getRouteNumber();

		Intent intent = Intents.Builder.with(this)
			.buildTimetableIntent(timetableUri, routeNumber, stationName, stationDirection);
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
