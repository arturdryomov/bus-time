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
		return TimetableFragment.newInstance(getTimetableUri());
	}

	private Uri getTimetableUri() {
		return getIntent().getParcelableExtra(Intents.Extras.URI);
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
}
