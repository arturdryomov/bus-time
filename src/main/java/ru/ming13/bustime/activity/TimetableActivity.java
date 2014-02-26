package ru.ming13.bustime.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import ru.ming13.bustime.R;
import ru.ming13.bustime.fragment.TimetableFragment;
import ru.ming13.bustime.model.Route;
import ru.ming13.bustime.model.Stop;
import ru.ming13.bustime.util.Fragments;
import ru.ming13.bustime.util.Frames;
import ru.ming13.bustime.util.Intents;
import ru.ming13.bustime.util.TitleBuilder;

public class TimetableActivity extends ActionBarActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (Frames.at(this).areAvailable()) {
			finish();
		} else {
			setUpUi();
		}
	}

	private void setUpUi() {
		setUpTitle();
		setUpSubtitle();

		setUpContainer();
		setUpTimetableFragment();
	}

	private void setUpTitle() {
		getSupportActionBar().setTitle(buildRouteTitle());
	}

	private String buildRouteTitle() {
		return getRoute().getNumber();
	}

	private Route getRoute() {
		return getIntent().getParcelableExtra(Intents.Extras.ROUTE);
	}

	private void setUpSubtitle() {
		getSupportActionBar().setSubtitle(buildStopSubtitle());
	}

	private String buildStopSubtitle() {
		return TitleBuilder.with(this).buildStopTitle(getStop());
	}

	private Stop getStop() {
		return getIntent().getParcelableExtra(Intents.Extras.STOP);
	}

	private void setUpContainer() {
		setContentView(R.layout.activity_container);
	}

	private void setUpTimetableFragment() {
		Fragments.Operator.at(this).set(buildTimetableFragment(), R.id.container_fragment);
	}

	private Fragment buildTimetableFragment() {
		return TimetableFragment.newInstance(getRoute(), getStop());
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
