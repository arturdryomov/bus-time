package ru.ming13.bustime.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ru.ming13.bustime.R;
import ru.ming13.bustime.fragment.StopMapFragment;
import ru.ming13.bustime.model.Stop;
import ru.ming13.bustime.util.Android;
import ru.ming13.bustime.util.Bartender;
import ru.ming13.bustime.util.Fragments;
import ru.ming13.bustime.util.Intents;
import ru.ming13.bustime.util.TitleBuilder;

public class StopMapActivity extends ActionBarActivity
{
	@InjectView(R.id.toolbar)
	Toolbar toolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_container_map);

		setUpInjections();

		setUpBars();
		setUpToolbar();
		setUpSubtitle();

		setUpMapFragment();
	}

	private void setUpInjections() {
		ButterKnife.inject(this);
	}

	private void setUpBars() {
		Bartender.at(this).showSystemBarsBackground();
	}

	private void setUpToolbar() {
		setUpToolbarPosition();

		setSupportActionBar(toolbar);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private void setUpToolbarPosition() {
		if (Android.isKitKatOrLater()) {
			RelativeLayout.LayoutParams toolbarParams = (RelativeLayout.LayoutParams) toolbar.getLayoutParams();

			toolbarParams.topMargin = Bartender.at(this).getStatusBarHeight();

			toolbar.setLayoutParams(toolbarParams);
		}
	}

	private void setUpSubtitle() {
		getSupportActionBar().setSubtitle(getStopTitle());
	}

	private String getStopTitle() {
		return TitleBuilder.with(this).buildStopTitle(getStop());
	}

	private Stop getStop() {
		return getIntent().getParcelableExtra(Intents.Extras.STOP);
	}

	private void setUpMapFragment() {
		Fragments.Operator.at(this).set(getMapFragment(), R.id.container_fragment);
	}

	private Fragment getMapFragment() {
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
