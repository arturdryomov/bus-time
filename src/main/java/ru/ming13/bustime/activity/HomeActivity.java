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

import com.squareup.otto.Subscribe;

import ru.ming13.bustime.R;
import ru.ming13.bustime.adapter.TabsPagerAdapter;
import ru.ming13.bustime.bus.BusProvider;
import ru.ming13.bustime.bus.RouteSelectedEvent;
import ru.ming13.bustime.bus.StationSelectedEvent;
import ru.ming13.bustime.provider.BusTimeContract;
import ru.ming13.bustime.task.StationInformationQueryingTask;
import ru.ming13.bustime.util.MapsUtil;
import ru.ming13.bustime.util.Intents;
import ru.ming13.bustime.util.Preferences;


public class HomeActivity extends ActionBarActivity implements ActionBar.TabListener, ViewPager.OnPageChangeListener,
	SearchView.OnCloseListener
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		setUpTabs();
		setUpTabsPager();

		setUpSelectedTab();
	}

	private void setUpTabs() {
		ActionBar actionBar = getSupportActionBar();

		actionBar.addTab(buildTab(R.string.title_routes), TabsPagerAdapter.TabsPositions.ROUTES);
		actionBar.addTab(buildTab(R.string.title_stations), TabsPagerAdapter.TabsPositions.STATIONS);
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
		return new TabsPagerAdapter(getSupportFragmentManager());
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
		setUpStationsSearchViewListeners(stationsSearchView);
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

	private void setUpStationsSearchViewListeners(SearchView stationsSearchView) {
		stationsSearchView.setOnCloseListener(this);
	}

	@Override
	public boolean onClose() {
		setUpOptionsMenu();

		return false;
	}

	private void setUpOptionsMenu() {
		supportInvalidateOptionsMenu();
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
	public boolean onPrepareOptionsMenu(Menu menu) {
		setUpStationsMapVisibility(menu);

		return super.onPrepareOptionsMenu(menu);
	}

	private void setUpStationsMapVisibility(Menu menu) {
		if (isStationsSearchInAction(menu)) {
			setUpStationsMapVisiblity(menu, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
		} else {
			setUpStationsMapVisiblity(menu, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
		}
	}

	private boolean isStationsSearchInAction(Menu menu) {
		return !getStationsSearchView(menu).isIconified();
	}

	private void setUpStationsMapVisiblity(Menu menu, int menuItemVisiblity) {
		MenuItem stationsMapMenuItem = menu.findItem(R.id.menu_stations_map);

		MenuItemCompat.setShowAsAction(stationsMapMenuItem, menuItemVisiblity);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case R.id.menu_stations_search:
				setUpOptionsMenu();
				return true;

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
	}

	@Override
	protected void onPause() {
		super.onPause();

		BusProvider.getBus().unregister(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		saveSeletectedTab();
	}

	private void saveSeletectedTab() {
		Preferences preferences = Preferences.getApplicationStateInstance(this);
		int selectedTabPosition = getSupportActionBar().getSelectedNavigationIndex();

		preferences.set(Preferences.Keys.SELECTED_TAB_POSITION, selectedTabPosition);
	}
}
