package ru.ming13.bustime.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;

import com.squareup.otto.Subscribe;

import ru.ming13.bustime.bus.BusProvider;
import ru.ming13.bustime.bus.RouteSelectedEvent;
import ru.ming13.bustime.fragment.StationRoutesFragment;
import ru.ming13.bustime.provider.BusTimeContract;
import ru.ming13.bustime.util.Fragments;
import ru.ming13.bustime.util.Intents;
import ru.ming13.bustime.util.TitleBuilder;

public class StationRoutesActivity extends ActionBarActivity
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
		String stationName = getStationName();
		String stationDirection = getStationDirection();

		return TitleBuilder.with(this).buildStationTitle(stationName, stationDirection);
	}

	private String getStationName() {
		return getIntent().getStringExtra(Intents.Extras.STATION_NAME);
	}

	private String getStationDirection() {
		return getIntent().getStringExtra(Intents.Extras.STATION_DIRECTION);
	}

	private void setUpFragment() {
		Fragments.Operator.add(this, buildFragment());
	}

	private Fragment buildFragment() {
		return StationRoutesFragment.newInstance(getRoutesUri());
	}

	private Uri getRoutesUri() {
		return getIntent().getParcelableExtra(Intents.Extras.URI);
	}

	@Override
	protected void onResume() {
		super.onResume();

		BusProvider.getBus().register(this);
	}

	@Subscribe
	public void onRouteSelected(RouteSelectedEvent event) {
		startTimetableActivity(event);
	}

	private void startTimetableActivity(RouteSelectedEvent event) {
		Uri timetableUri = getTimetableUri(event.getRouteId());
		String routeNumber = event.getRouteNumber();
		String stationName = getStationName();
		String stationDirection = getStationDirection();

		Intent intent = Intents.Builder.with(this)
			.buildTimetableIntent(timetableUri, routeNumber, stationName, stationDirection);

		startActivity(intent);
	}

	private Uri getTimetableUri(long routeId) {
		return BusTimeContract.Stations.buildStationTimetableUri(getRoutesUri(), routeId);
	}

	@Override
	protected void onPause() {
		super.onPause();

		BusProvider.getBus().unregister(this);
	}
}
