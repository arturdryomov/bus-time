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
import ru.ming13.bustime.model.Route;
import ru.ming13.bustime.model.Stop;
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
		return TitleBuilder.with(this).buildStopTitle(getStop());
	}

	private Stop getStop() {
		return getIntent().getParcelableExtra(Intents.Extras.STOP);
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
		startTimetableActivity(event.getRoute());
	}

	private void startTimetableActivity(Route route) {
		Uri timetableUri = BusTimeContract.Stops.buildStopTimetableUri(getRoutesUri(), route.getId());

		Intent intent = Intents.Builder.with(this).buildTimetableIntent(timetableUri, route, getStop());
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
