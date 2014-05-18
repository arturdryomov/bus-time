package ru.ming13.bustime.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import ru.ming13.bustime.R;
import ru.ming13.bustime.fragment.RouteMapFragment;
import ru.ming13.bustime.model.Route;
import ru.ming13.bustime.util.Bartender;
import ru.ming13.bustime.util.Fragments;
import ru.ming13.bustime.util.Intents;
import ru.ming13.bustime.util.TitleBuilder;

public class RouteMapActivity extends ActionBarActivity
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
		getSupportActionBar().setSubtitle(buildRouteTitle());
	}

	private String buildRouteTitle() {
		return TitleBuilder.with(this).buildRouteTitle(getRoute());
	}

	private Route getRoute() {
		return getIntent().getParcelableExtra(Intents.Extras.ROUTE);
	}

	private void setUpMapFragment() {
		Fragments.Operator.at(this).set(buildMapFragment(), R.id.container_fragment);
	}

	private Fragment buildMapFragment() {
		return RouteMapFragment.newInstance(getRoute());
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
