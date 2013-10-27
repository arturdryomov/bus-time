package ru.ming13.bustime.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;

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
		Fragments.Operator.add(this, buildFragment());
	}

	private Fragment buildFragment() {
		return RouteStationsFragment.newInstance(getStationsUri());
	}

	private Uri getStationsUri() {
		return getIntent().getParcelableExtra(Intents.Extras.URI);
	}

	@Override
	protected void onResume() {
		super.onResume();

		BusProvider.getBus().register(this);
	}

	@Subscribe
	public void onStationSelected(StationSelectedEvent event) {
		startTimetableActivity(event);
	}

	private void startTimetableActivity(StationSelectedEvent event) {
		Uri timetableUri = getTimetableUri(event.getStationId());
		String routeNumber = getRouteNumber();
		String stationName = event.getStationName();
		String stationDirection = event.getStationDirection();

		Intent intent = Intents.Builder.with(this)
			.buildTimetableIntent(timetableUri, routeNumber, stationName, stationDirection);

		startActivity(intent);
	}

	private Uri getTimetableUri(long stationId) {
		return BusTimeContract.Routes.buildRouteTimetableUri(getStationsUri(), stationId);
	}

	@Override
	protected void onPause() {
		super.onPause();

		BusProvider.getBus().unregister(this);
	}
}
