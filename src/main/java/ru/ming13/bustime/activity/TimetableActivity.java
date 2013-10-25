package ru.ming13.bustime.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import ru.ming13.bustime.fragment.TimetableFragment;
import ru.ming13.bustime.util.Fragments;
import ru.ming13.bustime.util.Intents;
import ru.ming13.bustime.util.TitleBuilder;

public class TimetableActivity extends ActionBarActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setUpTitle();
		setUpSubtitle();
		setUpFragment();
	}

	private void setUpTitle() {
		getSupportActionBar().setTitle(buildTitle());
	}

	private String buildTitle() {
		return getRouteNumber();
	}

	private String getRouteNumber() {
		return getIntent().getStringExtra(Intents.Extras.ROUTE_NUMBER);
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
		return TimetableFragment.newInstance(getTimetableUri());
	}

	private Uri getTimetableUri() {
		return getIntent().getParcelableExtra(Intents.Extras.URI);
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
		// It is not possible to use NavUtils here because of multiple possible activities.

		finish();
	}
}
