package ru.ming13.bustime.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.squareup.otto.Subscribe;

import ru.ming13.bustime.R;
import ru.ming13.bustime.bus.BusProvider;
import ru.ming13.bustime.bus.RouteSelectedEvent;
import ru.ming13.bustime.fragment.MessageFragment;
import ru.ming13.bustime.fragment.StopRoutesFragment;
import ru.ming13.bustime.fragment.TimetableFragment;
import ru.ming13.bustime.model.Route;
import ru.ming13.bustime.model.Stop;
import ru.ming13.bustime.util.Fragments;
import ru.ming13.bustime.util.Frames;
import ru.ming13.bustime.util.Intents;
import ru.ming13.bustime.util.TitleBuilder;

public class StopRoutesActivity extends ActionBarActivity
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

		setUpRoutesFragment();
	}

	private void setUpTitle() {
		getSupportActionBar().setTitle(buildStopTitle());
	}

	private String buildStopTitle() {
		return TitleBuilder.with(this).buildStopTitle(getStop());
	}

	private Stop getStop() {
		return getIntent().getParcelableExtra(Intents.Extras.STOP);
	}

	private void setUpFrames() {
		setContentView(R.layout.activity_frames);

		Frames.at(this).setLeftFrameTitle(getString(R.string.title_routes));
		Frames.at(this).setRightFrameTitle(getString(R.string.title_timetable));
	}

	private void setUpEmptyFrame() {
		Fragments.Operator.set(this, buildMessageFragment(), R.id.container_right_frame);
	}

	private Fragment buildMessageFragment() {
		return MessageFragment.newInstance(getString(R.string.message_no_route));
	}

	private void setUpRoutesFragment() {
		if (Frames.at(this).areAvailable()) {
			Fragments.Operator.set(this, buildRoutesFragment(), R.id.container_left_frame);
		} else {
			Fragments.Operator.set(this, buildRoutesFragment(), R.id.container_fragment);
		}
	}

	private void setUpSubtitle() {
		getSupportActionBar().setSubtitle(buildStopTitle());
	}

	private void setUpContainer() {
		setContentView(R.layout.activity_container);
	}

	private Fragment buildRoutesFragment() {
		return StopRoutesFragment.newInstance(getStop());
	}

	@Subscribe
	public void onRouteSelected(RouteSelectedEvent event) {
		setUpTimetable(event.getRoute());
	}

	private void setUpTimetable(Route route) {
		if (Frames.at(this).areAvailable()) {
			setUpTimetableFragment(route);
		} else {
			startTimetableActivity(route);
		}
	}

	private void setUpTimetableFragment(Route route) {
		Fragments.Operator.reset(this, buildTimetableFragment(route), R.id.container_right_frame);
	}

	private Fragment buildTimetableFragment(Route route) {
		return TimetableFragment.newInstance(route, getStop());
	}

	private void startTimetableActivity(Route route) {
		Intent intent = Intents.Builder.with(this).buildTimetableIntent(route, getStop());
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
