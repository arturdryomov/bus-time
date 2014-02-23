package ru.ming13.bustime.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.squareup.otto.Subscribe;

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
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setUpUi();
	}

	private void setUpUi() {
		if (Frames.at(this).areAvailable()) {
			setUpTitle();
			setUpFrames();
			setUpEmptyFrame();
		} else {
			setUpSubtitle();
			setUpContainer();
		}

		setUpStopsFragment();
	}

	private void setUpTitle() {
		getSupportActionBar().setTitle(buildRouteTitle());
	}

	private String buildRouteTitle() {
		return TitleBuilder.with(this).buildRouteTitle(getRoute());
	}

	private Route getRoute() {
		return getIntent().getParcelableExtra(Intents.Extras.ROUTE);
	}

	private void setUpFrames() {
		setContentView(R.layout.activity_frames);

		Frames.at(this).setLeftFrameTitle(getString(R.string.title_stops));
		Frames.at(this).setRightFrameTitle(getString(R.string.title_timetable));
	}

	private void setUpEmptyFrame() {
		Fragments.Operator.at(this).set(buildMessageFragment(), R.id.container_right_frame);
	}

	private Fragment buildMessageFragment() {
		return MessageFragment.newInstance(getString(R.string.message_no_stop));
	}

	private void setUpSubtitle() {
		getSupportActionBar().setSubtitle(buildRouteTitle());
	}

	private void setUpContainer() {
		setContentView(R.layout.activity_container);
	}

	private void setUpStopsFragment() {
		if (Frames.at(this).areAvailable()) {
			Fragments.Operator.at(this).set(buildStopsFragment(), R.id.container_left_frame);
		} else {
			Fragments.Operator.at(this).set(buildStopsFragment(), R.id.container_fragment);
		}
	}

	private Fragment buildStopsFragment() {
		return RouteStopsFragment.newInstance(getRoute());
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
		Fragments.Operator.at(this).reset(buildTimetableFragment(stop), R.id.container_right_frame);
	}

	private Fragment buildTimetableFragment(Stop stop) {
		return TimetableFragment.newInstance(getRoute(), stop);
	}

	private void startTimetableActivity(Stop stop) {
		Intent intent = Intents.Builder.with(this).buildTimetableIntent(getRoute(), stop);
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
