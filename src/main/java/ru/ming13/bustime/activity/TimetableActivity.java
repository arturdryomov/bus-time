package ru.ming13.bustime.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.ButterKnife;
import butterknife.InjectView;
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
	@InjectView(R.id.toolbar)
	Toolbar toolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_container);

		if (Frames.at(this).areAvailable()) {
			finish();
			return;
		}

		setUpInjections();

		setUpUi();
	}

	private void setUpInjections() {
		ButterKnife.inject(this);
	}

	private void setUpUi() {
		setUpToolbar();

		setUpTitle();
		setUpSubtitle();

		setUpTimetableFragment();
	}

	private void setUpToolbar() {
		setSupportActionBar(toolbar);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private void setUpTitle() {
		getSupportActionBar().setTitle(getRouteTitle());
	}

	private String getRouteTitle() {
		return getRoute().getNumber();
	}

	private Route getRoute() {
		return getIntent().getParcelableExtra(Intents.Extras.ROUTE);
	}

	private void setUpSubtitle() {
		getSupportActionBar().setSubtitle(getStopSubtitle());
	}

	private String getStopSubtitle() {
		return TitleBuilder.with(this).buildStopTitle(getStop());
	}

	private Stop getStop() {
		return getIntent().getParcelableExtra(Intents.Extras.STOP);
	}

	private void setUpTimetableFragment() {
		Fragments.Operator.at(this).set(getTimetableFragment(), R.id.container_fragment);
	}

	private Fragment getTimetableFragment() {
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
