package ru.ming13.bustime.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.f2prateek.dart.Dart;
import com.f2prateek.dart.InjectExtra;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ru.ming13.bustime.R;
import ru.ming13.bustime.bus.BusProvider;
import ru.ming13.bustime.bus.StopSelectedEvent;
import ru.ming13.bustime.fragment.MessageFragment;
import ru.ming13.bustime.fragment.RouteStopsFragment;
import ru.ming13.bustime.fragment.TimetableFragment;
import ru.ming13.bustime.model.Route;
import ru.ming13.bustime.model.Stop;
import ru.ming13.bustime.util.Fragments;
import ru.ming13.bustime.util.Frames;
import ru.ming13.bustime.util.Intents;
import ru.ming13.bustime.util.TitleBuilder;

public class RouteStopsActivity extends ActionBarActivity
{
	@InjectView(R.id.toolbar)
	Toolbar toolbar;

	@InjectExtra(Intents.Extras.ROUTE)
	Route route;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_container);

		setUpInjections();

		setUpUi();
	}

	private void setUpInjections() {
		ButterKnife.inject(this);

		Dart.inject(this);
	}

	private void setUpUi() {
		setUpToolbar();

		if (Frames.at(this).areAvailable()) {
			setUpTitle();
			setUpFrameTitles();
			setUpMessageFragment();
		} else {
			setUpSubtitle();
		}

		setUpStopsFragment();
	}

	private void setUpToolbar() {
		setSupportActionBar(toolbar);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private void setUpTitle() {
		getSupportActionBar().setTitle(getRouteTitle());
	}

	private String getRouteTitle() {
		return TitleBuilder.with(this).buildRouteTitle(route);
	}

	private void setUpFrameTitles() {
		Frames.at(this).setLeftFrameTitle(R.string.title_stops);
		Frames.at(this).setRightFrameTitle(R.string.title_timetable);
	}

	private void setUpMessageFragment() {
		Fragments.Operator.at(this).set(getMessageFragment(), R.id.container_right_frame);
	}

	private Fragment getMessageFragment() {
		return MessageFragment.newInstance(getString(R.string.message_no_stop));
	}

	private void setUpSubtitle() {
		getSupportActionBar().setSubtitle(getRouteTitle());
	}

	private void setUpStopsFragment() {
		if (Frames.at(this).areAvailable()) {
			Fragments.Operator.at(this).set(getStopsFragment(), R.id.container_left_frame);
		} else {
			Fragments.Operator.at(this).set(getStopsFragment(), R.id.container_fragment);
		}
	}

	private Fragment getStopsFragment() {
		return RouteStopsFragment.newInstance(route);
	}

	@Subscribe
	public void onStopSelected(StopSelectedEvent event) {
		setUpTimetable(event.getStop());
	}

	private void setUpTimetable(Stop stop) {
		if (Frames.at(this).areAvailable()) {
			setUpTimetableFragment(stop);
		} else {
			startTimetableActivity(stop);
		}
	}

	private void setUpTimetableFragment(Stop stop) {
		if (Fragments.Operator.at(this).get(R.id.container_right_frame) instanceof MessageFragment) {
			Fragments.Operator.at(this).resetSliding(getTimetableFragment(stop), R.id.container_right_frame);
		} else {
			Fragments.Operator.at(this).resetFading(getTimetableFragment(stop), R.id.container_right_frame);
		}
	}

	private Fragment getTimetableFragment(Stop stop) {
		return TimetableFragment.newInstance(route, stop);
	}

	private void startTimetableActivity(Stop stop) {
		Intent intent = Intents.Builder.with(this).buildTimetableIntent(route, stop);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.action_bar_route_stops, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case android.R.id.home:
				finish();
				return true;

			case R.id.menu_map:
				startRouteStopsMapActivity();
				return true;

			default:
				return super.onOptionsItemSelected(menuItem);
		}
	}

	private void startRouteStopsMapActivity() {
		Intent intent = Intents.Builder.with(this).buildRouteMapIntent(route);
		startActivity(intent);
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
