package ru.ming13.bustime.activity;


import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ViewAnimator;

import com.squareup.otto.Subscribe;

import ru.ming13.bustime.R;
import ru.ming13.bustime.adapter.TabPagerAdapter;
import ru.ming13.bustime.bus.BusEventsCollector;
import ru.ming13.bustime.bus.BusProvider;
import ru.ming13.bustime.bus.RouteSelectedEvent;
import ru.ming13.bustime.bus.StationSelectedEvent;
import ru.ming13.bustime.bus.UpdatesAcceptedEvent;
import ru.ming13.bustime.bus.UpdatesAvailableEvent;
import ru.ming13.bustime.bus.UpdatesDiscardedEvent;
import ru.ming13.bustime.bus.UpdatesFinishedEvent;
import ru.ming13.bustime.fragment.UpdatesBannerFragment;
import ru.ming13.bustime.provider.BusTimeContract;
import ru.ming13.bustime.task.DatabaseUpdateCheckingTask;
import ru.ming13.bustime.task.DatabaseUpdatingTask;
import ru.ming13.bustime.task.StationInformationQueryingTask;
import ru.ming13.bustime.util.Fragments;
import ru.ming13.bustime.util.Intents;
import ru.ming13.bustime.util.MapsUtil;
import ru.ming13.bustime.util.Preferences;


public class HomeActivity extends ActionBarActivity implements ActionBar.TabListener, ViewPager.OnPageChangeListener
{
	private static final class SavedState
	{
		private SavedState() {
		}

		public static final String PROGRESS_VISIBLE = "PROGRESS_VISIBLE";
		public static final String UPDATES_DONE = "UPDATES_DONE";
	}

	private boolean areUpdatesDone;
	private boolean isProgressVisible;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		setUpSavedState(savedInstanceState);

		setUpTabs();
		setUpTabsPager();

		setUpSelectedTab();

		setUpProgress();
		setUpDatabaseUpdates();
	}

	private void setUpSavedState(Bundle state) {
		if (state == null) {
			return;
		}

		areUpdatesDone = loadUpdatesDone(state);
		isProgressVisible = loadProgressVisible(state);
	}

	private boolean loadUpdatesDone(Bundle state) {
		return state.getBoolean(SavedState.UPDATES_DONE);
	}

	private boolean loadProgressVisible(Bundle state) {
		return state.getBoolean(SavedState.PROGRESS_VISIBLE);
	}

	private void setUpTabs() {
		ActionBar actionBar = getSupportActionBar();

		actionBar.addTab(buildTab(R.string.title_routes), TabPagerAdapter.TabPosition.ROUTES);
		actionBar.addTab(buildTab(R.string.title_stations), TabPagerAdapter.TabPosition.STATIONS);
	}

	private ActionBar.Tab buildTab(int tabTitleResourceId) {
		ActionBar.Tab tab = getSupportActionBar().newTab();

		tab.setTabListener(this);
		tab.setText(tabTitleResourceId);

		return tab;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		getTabsPager().setCurrentItem(tab.getPosition());
	}

	private ViewPager getTabsPager() {
		return (ViewPager) findViewById(R.id.pager_tabs);
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	private void setUpTabsPager() {
		ViewPager tabsPager = getTabsPager();

		tabsPager.setAdapter(buildTabsPagerAdapter());
		tabsPager.setOnPageChangeListener(this);
	}

	private PagerAdapter buildTabsPagerAdapter() {
		return new TabPagerAdapter(getSupportFragmentManager());
	}

	@Override
	public void onPageSelected(int position) {
		getSupportActionBar().setSelectedNavigationItem(position);
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
	}

	@Override
	public void onPageScrollStateChanged(int position) {
	}

	private void setUpSelectedTab() {
		Preferences preferences = Preferences.getApplicationStateInstance(this);
		int selectedTabPosition = preferences.getInt(Preferences.Keys.SELECTED_TAB_POSITION);

		getSupportActionBar().setSelectedNavigationItem(selectedTabPosition);
	}

	private void setUpProgress() {
		if (isProgressVisible) {
			showProgress();
		}
	}

	private void showProgress() {
		ViewAnimator animator = (ViewAnimator) findViewById(R.id.animator);
		animator.setDisplayedChild(animator.indexOfChild(findViewById(R.id.progress)));
	}

	private void setUpDatabaseUpdates() {
		if (!areUpdatesDone) {
			DatabaseUpdateCheckingTask.execute(this);

			saveUpdatesDone();
		}
	}

	private void saveUpdatesDone() {
		areUpdatesDone = true;
	}

	@Subscribe
	public void onUpdatesAvailable(UpdatesAvailableEvent event) {
		showUpdatesBanner();
	}

	private void showUpdatesBanner() {
		if (!isUpdatesBannerVisible()) {
			UpdatesBannerFragment.newInstance().show(getSupportFragmentManager());
		}
	}

	private boolean isUpdatesBannerVisible() {
		return getUpdatesBanner() != null;
	}

	private UpdatesBannerFragment getUpdatesBanner() {
		return (UpdatesBannerFragment) Fragments.Operator.find(this, UpdatesBannerFragment.TAG);
	}

	@Subscribe
	public void onUpdatesAccepted(UpdatesAcceptedEvent event) {
		hideUpdatesBanner();

		startUpdates();
	}

	private void hideUpdatesBanner() {
		getUpdatesBanner().hide(getSupportFragmentManager());
	}

	private void startUpdates() {
		showProgress();

		DatabaseUpdatingTask.execute(this);
	}

	@Subscribe
	public void onUpdatesFinished(UpdatesFinishedEvent event) {
		finishUpdates();
	}

	private void finishUpdates() {
		hideProgress();
	}

	private void hideProgress() {
		ViewAnimator animator = (ViewAnimator) findViewById(R.id.animator);
		animator.setDisplayedChild(animator.indexOfChild(findViewById(R.id.pager_tabs)));
	}

	@Subscribe
	public void onUpdatesDiscarded(UpdatesDiscardedEvent event) {
		hideUpdatesBanner();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		if (Intent.ACTION_VIEW.equals(intent.getAction())) {
			startShowingSearchResult(intent);
		}
	}

	private void startShowingSearchResult(Intent searchResultIntent) {
		long stationId = BusTimeContract.Stations.getSearchStationId(searchResultIntent.getData());

		StationInformationQueryingTask.execute(this, stationId);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.action_bar_home, menu);

		setUpStationsSearch(menu);
		setUpStationsMap(menu);

		return super.onCreateOptionsMenu(menu);
	}

	private void setUpStationsSearch(Menu menu) {
		SearchView stationsSearchView = getStationsSearchView(menu);

		setUpStationsSearchInformation(stationsSearchView);
		setUpStationsSearchView(stationsSearchView);
	}

	private SearchView getStationsSearchView(Menu menu) {
		MenuItem stationsSearchMenuItem = menu.findItem(R.id.menu_stations_search);
		return (SearchView) MenuItemCompat.getActionView(stationsSearchMenuItem);
	}

	private void setUpStationsSearchInformation(SearchView stationsSearchView) {
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		stationsSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
	}

	private void setUpStationsSearchView(SearchView stationsSearchView) {
		LinearLayout stationsSearchPlate = (LinearLayout) stationsSearchView.findViewById(R.id.search_plate);
		EditText stationsSearchQueryEdit = (EditText) stationsSearchView.findViewById(R.id.search_src_text);

		stationsSearchPlate.setBackgroundResource(R.drawable.abc_textfield_search_default_holo_dark);
		stationsSearchQueryEdit.setHintTextColor(getResources().getColor(R.color.text_hint_search));
	}

	private void setUpStationsMap(Menu menu) {
		if (!MapsUtil.with(this).areMapsHardwareAvailable()) {
			disableStationsMap(menu);
		}
	}

	private void disableStationsMap(Menu menu) {
		MenuItem stationsMapMenuItem = menu.findItem(R.id.menu_stations_map);
		stationsMapMenuItem.setVisible(false);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case R.id.menu_stations_map:
				startStationsMapActivity();
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

	private void startStationsMapActivity() {
		if (MapsUtil.with(this).areMapsSoftwareAvailable()) {
			Intent intent = Intents.Builder.with(this).buildStationsMapIntent();
			startActivity(intent);
		} else {
			MapsUtil.with(this).showErrorDialog(getSupportFragmentManager());
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
		startActivity(intent);
	}

	@Subscribe
	public void onRouteSelected(RouteSelectedEvent event) {
		long routeId = event.getRouteId();
		String routeNumber = event.getRouteNumber();
		String routeDescription = event.getRouteDescription();

		startRouteStationsActivity(routeId, routeNumber, routeDescription);
	}

	private void startRouteStationsActivity(long routeId, String routeNumber, String routeDescription) {
		Uri routeStationsUri = BusTimeContract.Routes.buildRouteStationsUri(routeId);

		Intent intent = Intents.Builder.with(this)
			.buildRouteStationsIntent(routeStationsUri, routeNumber, routeDescription);
		startActivity(intent);
	}

	@Subscribe
	public void onStationSelected(StationSelectedEvent event) {
		long stationId = event.getStationId();
		String stationName = event.getStationName();
		String stationDirection = event.getStationDirection();

		startStationRoutesActivity(stationId, stationName, stationDirection);
	}

	private void startStationRoutesActivity(long stationId, String stationName, String stationDirection) {
		Uri stationRoutesUri = BusTimeContract.Stations.buildStationRoutesUri(stationId);

		Intent intent = Intents.Builder.with(this)
			.buildStationRoutesIntent(stationRoutesUri, stationName, stationDirection);
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

		saveUpdatesDone(outState);
		saveProgressVisible(outState);
	}

	private void saveUpdatesDone(Bundle state) {
		state.putBoolean(SavedState.UPDATES_DONE, areUpdatesDone);
	}

	private void saveProgressVisible(Bundle state) {
		ViewAnimator animator = (ViewAnimator) findViewById(R.id.animator);

		int visibleView = animator.getDisplayedChild();
		int progressView = animator.indexOfChild(findViewById(R.id.progress));

		state.putBoolean(SavedState.PROGRESS_VISIBLE, visibleView == progressView);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		saveSelectedTab();
	}

	private void saveSelectedTab() {
		Preferences preferences = Preferences.getApplicationStateInstance(this);
		int selectedTabPosition = getSupportActionBar().getSelectedNavigationIndex();

		preferences.set(Preferences.Keys.SELECTED_TAB_POSITION, selectedTabPosition);
	}
}
