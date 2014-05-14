package ru.ming13.bustime.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import ru.ming13.bustime.R;
import ru.ming13.bustime.fragment.StopMapFragment;
import ru.ming13.bustime.model.Stop;
import ru.ming13.bustime.util.Bartender;
import ru.ming13.bustime.util.Fragments;
import ru.ming13.bustime.util.Intents;
import ru.ming13.bustime.util.TitleBuilder;

public class StopMapActivity extends ActionBarActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_container);

		setUpBars();
		setUpSubtitle();

		setUpMapFragment();
	}

	private void setUpBars() {
		Bartender.at(this).showSystemBarsBackground();
	}

	private void setUpSubtitle() {
		getSupportActionBar().setSubtitle(buildStopTitle());
	}

	private String buildStopTitle() {
		return TitleBuilder.with(this).buildStopTitle(getStop());
	}

	private Stop getStop() {
		return getIntent().getParcelableExtra(Intents.Extras.STOP);
	}

	private void setUpMapFragment() {
		Fragments.Operator.at(this).set(buildMapFragment(), R.id.container_fragment);
	}

	private Fragment buildMapFragment() {
		return StopMapFragment.newInstance(getStop());
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
