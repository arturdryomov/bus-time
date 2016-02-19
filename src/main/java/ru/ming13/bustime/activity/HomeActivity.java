package ru.ming13.bustime.activity;

import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.nispok.snackbar.listeners.EventListener;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import icepick.Icepick;
import icepick.State;
import ru.ming13.bustime.R;
import ru.ming13.bustime.adapter.TabPagerAdapter;
import ru.ming13.bustime.bus.BusEventsCollector;
import ru.ming13.bustime.bus.BusProvider;
import ru.ming13.bustime.bus.DatabaseUpdateAvailableEvent;
import ru.ming13.bustime.bus.DatabaseUpdateFinishedEvent;
import ru.ming13.bustime.bus.RouteSelectedEvent;
import ru.ming13.bustime.bus.StopLoadedEvent;
import ru.ming13.bustime.bus.StopSelectedEvent;
import ru.ming13.bustime.fragment.RoutesFragment;
import ru.ming13.bustime.fragment.StopsFragment;
import ru.ming13.bustime.model.Route;
import ru.ming13.bustime.model.Stop;
import ru.ming13.bustime.provider.BusTimeContract;
import ru.ming13.bustime.task.DatabaseUpdateCheckingTask;
import ru.ming13.bustime.task.DatabaseUpdatingTask;
import ru.ming13.bustime.task.StopLoadingTask;
import ru.ming13.bustime.util.Fragments;
import ru.ming13.bustime.util.Frames;
import ru.ming13.bustime.util.Intents;
import ru.ming13.bustime.util.Maps;
import ru.ming13.bustime.util.Preferences;
import ru.ming13.bustime.util.ViewDirector;
import ru.ming13.bustime.view.TabLayout;

public class HomeActivity extends AppCompatActivity implements EventListener, ActionClickListener
{
	@Bind(R.id.toolbar)
	@Nullable
	Toolbar toolbar;

	@Bind(R.id.layout_tabs)
	@Nullable
	TabLayout tabLayout;

	@Bind(R.id.pager_tabs)
	@Nullable
	ViewPager tabPager;

	@State
	boolean isDatabaseUpdateDone;

	@State
	boolean isProgressVisible;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		setUpBindings();

		setUpState(savedInstanceState);

		setUpUi();

		setUpDatabaseUpdate();
	}

	private void setUpBindings() {
		ButterKnife.bind(this);
	}

	private void setUpState(Bundle state) {
		Icepick.restoreInstanceState(this, state);
	}

	private void setUpUi() {
		setUpToolbar();

		if (Frames.at(this).areAvailable()) {
			setUpFrames();
		} else {
			setUpTabs();
			setUpTabsSelection();
		}

		setUpProgress();
	}

	private void setUpToolbar() {
		setSupportActionBar(toolbar);
	}

	private void setUpFrames() {
		Frames.at(this).setLeftFrameTitle(R.string.title_routes);
		Frames.at(this).setRightFrameTitle(R.string.title_stops);

		Fragments.Operator.at(this).set(getRoutesFragment(), R.id.container_left_frame);
		Fragments.Operator.at(this).set(getStopsFragment(), R.id.container_right_frame);
	}

	private Fragment getRoutesFragment() {
		return RoutesFragment.newInstance();
	}

	private Fragment getStopsFragment() {
		return StopsFragment.newInstance();
	}

	private void setUpTabs() {
		tabPager.setAdapter(new TabPagerAdapter(this, getSupportFragmentManager()));
		tabLayout.setTabPager(getSupportActionBar().getThemedContext(), tabPager);
	}

	private void setUpTabsSelection() {
		int selectedTabPosition = Preferences.of(this).getHomeTabPositionPreference().get();

		tabPager.setCurrentItem(selectedTabPosition);
	}

	private void setUpProgress() {
		if (isProgressVisible) {
			showProgress();
		}
	}

	private void showProgress() {
		ViewDirector.of(this, R.id.animator).show(R.id.progress);

		this.isProgressVisible = true;
	}

	private void setUpDatabaseUpdate() {
		if (!isDatabaseUpdateDone) {
			DatabaseUpdateCheckingTask.execute(this);
		}
	}

	@Subscribe
	public void onDatabaseUpdateAvailable(DatabaseUpdateAvailableEvent event) {
		showDatabaseUpdateBanner();
	}

	private void showDatabaseUpdateBanner() {
		Snackbar.with(this)
			.duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
			.text(R.string.message_updates)
			.actionLabel(R.string.button_download)
			.actionColorResource(R.color.background_primary)
			.actionListener(this)
			.eventListener(this)
			.show(this);
	}

	@Override
	public void onShow(Snackbar snackbar) {
	}

	@Override
	public void onShown(Snackbar snackbar) {
	}

	@Override
	public void onDismiss(Snackbar snackbar) {
	}

	@Override
	public void onDismissed(Snackbar snackbar) {
		this.isDatabaseUpdateDone = true;
	}

	@Override
	public void onActionClicked(Snackbar snackbar) {
		this.isDatabaseUpdateDone = true;

		startDatabaseUpdate();
	}

	private void startDatabaseUpdate() {
		showProgress();

		DatabaseUpdatingTask.execute(this);
	}

	@Subscribe
	public void onDatabaseUpdateFinished(DatabaseUpdateFinishedEvent event) {
		finishDatabaseUpdate();
	}

	private void finishDatabaseUpdate() {
		hideProgress();
	}

	private void hideProgress() {
		ViewDirector.of(this, R.id.animator).show(R.id.content);

		this.isProgressVisible = false;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		if (Intent.ACTION_VIEW.equals(intent.getAction())) {
			startShowingSearchResult(intent);
		}
	}

	private void startShowingSearchResult(Intent searchResultIntent) {
		long stopId = BusTimeContract.Stops.getStopsSearchId(searchResultIntent.getData());

		StopLoadingTask.execute(this, stopId);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.action_bar_home, menu);

		setUpStopsSearch(menu);
		setUpStopsMap(menu);

		return super.onCreateOptionsMenu(menu);
	}

	private void setUpStopsSearch(Menu menu) {
		SearchView stopsSearchView = getStopsSearchView(menu);

		setUpStopsSearchInformation(stopsSearchView);
		setUpStopsSearchView(stopsSearchView);
	}

	private SearchView getStopsSearchView(Menu menu) {
		MenuItem stopsSearchMenuItem = menu.findItem(R.id.menu_search);

		return (SearchView) MenuItemCompat.getActionView(stopsSearchMenuItem);
	}

	private void setUpStopsSearchInformation(SearchView stopsSearchView) {
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

		stopsSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
	}

	private void setUpStopsSearchView(SearchView stopsSearchView) {
		LinearLayout stopsSearchPlate = ButterKnife.findById(stopsSearchView, R.id.search_plate);
		EditText stopsSearchQueryEdit = ButterKnife.findById(stopsSearchView, R.id.search_src_text);

		stopsSearchPlate.setBackgroundResource(R.drawable.abc_textfield_search_material);
		stopsSearchQueryEdit.setHintTextColor(getResources().getColor(R.color.text_hint_light));
	}

	private void setUpStopsMap(Menu menu) {
		if (!Maps.at(this).areHardwareAvailable()) {
			menu.findItem(R.id.menu_map).setVisible(false);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case R.id.menu_map:
				startStopsMapActivity();
				return true;

			case R.id.menu_rate_application:
				startApplicationRating();
				return true;

			case R.id.menu_send_feedback:
				startFeedbackSending();
				return true;

			default:
				return super.onOptionsItemSelected(menuItem);
		}
	}

	private void startStopsMapActivity() {
		if (Maps.at(this).areSoftwareAvailable()) {
			Intent intent = Intents.Builder.with(this).buildStopsMapIntent();
			startActivity(intent);
		} else {
			Maps.at(this).showErrorDialog();
		}
	}

	private void startApplicationRating() {
		try {
			Intent intent = Intents.Builder.with(this).buildGooglePlayAppIntent();
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Intent intent = Intents.Builder.with(this).buildGooglePlayWebIntent();
			startActivity(intent);
		}
	}

	private void startFeedbackSending() {
		Intent intent = Intents.Builder.with(this).buildFeedbackIntent();

		try {
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			startActivity(Intent.createChooser(intent, null));
		}
	}

	@Subscribe
	public void onRouteSelected(RouteSelectedEvent event) {
		startRouteStopsActivity(event.getRoute());
	}

	private void startRouteStopsActivity(Route route) {
		Intent intent = Intents.Builder.with(this).buildRouteStopsIntent(route);
		startActivity(intent);
	}

	@Subscribe
	public void onStopSelected(StopSelectedEvent event) {
		startStopRoutesActivity(event.getStop());
	}

	@Subscribe
	public void onStopLoaded(StopLoadedEvent event) {
		startStopRoutesActivity(event.getStop());
	}

	private void startStopRoutesActivity(Stop stop) {
		Intent intent = Intents.Builder.with(this).buildStopRoutesIntent(stop);
		startActivity(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();

		BusProvider.getBus().register(this);

		BusEventsCollector.getInstance().postCollectedEvents();
	}

	@Override
	protected void onPause() {
		super.onPause();

		BusProvider.getBus().unregister(this);

		BusProvider.getBus().register(BusEventsCollector.getInstance());
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		tearDownState(outState);
	}

	private void tearDownState(Bundle state) {
		Icepick.saveInstanceState(this, state);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		tearDownPreferences();
	}

	private void tearDownPreferences() {
		if (areTabsAvailable()) {
			int selectedTabPosition = tabPager.getCurrentItem();

			Preferences.of(this).getHomeTabPositionPreference().set(selectedTabPosition);
		}
	}

	private boolean areTabsAvailable() {
		// Frames check will not work because orientation is already changed at this point

		return tabPager != null;
	}
}
