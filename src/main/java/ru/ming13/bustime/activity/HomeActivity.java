package ru.ming13.bustime.activity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import com.squareup.otto.Subscribe;

import ru.ming13.bustime.R;
import ru.ming13.bustime.adapter.TabsPagerAdapter;
import ru.ming13.bustime.bus.BusProvider;
import ru.ming13.bustime.bus.RouteSelectedEvent;
import ru.ming13.bustime.bus.StationSelectedEvent;
import ru.ming13.bustime.provider.BusTimeContract;
import ru.ming13.bustime.util.Intents;


public class HomeActivity extends ActionBarActivity implements ActionBar.TabListener, ViewPager.OnPageChangeListener
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		setUpTabs();
		setUpTabsPager();
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

	@Override
	protected void onResume() {
		super.onResume();

		BusProvider.getBus().register(this);
	}

	@Subscribe
	public void onRouteSelected(RouteSelectedEvent event) {
		startRouteStationsActivity(event);
	}

	private void startRouteStationsActivity(RouteSelectedEvent event) {
		Uri routeStationsUri = getRouteStationsUri(event.getRouteId());
		String routeNumber = event.getRouteNumber();
		String routeDescription = event.getRouteDescription();

		Intent intent = Intents.getBuilder(this)
			.buildRouteStationsIntent(routeStationsUri, routeNumber, routeDescription);

		startActivity(intent);
	}

	private Uri getRouteStationsUri(long routeId) {
		return BusTimeContract.Routes.buildRouteStationsUri(routeId);
	}

	@Subscribe
	public void onStationSelected(StationSelectedEvent event) {
		startStationRoutesActivity(event);
	}

	private void startStationRoutesActivity(StationSelectedEvent event) {
		Uri stationRoutesUri = getStationRoutesUri(event.getStationId());
		String stationName = event.getStationName();
		String stationDirection = event.getStationDirection();

		Intent intent = Intents.getBuilder(this)
			.buildStationRoutesIntent(stationRoutesUri, stationName, stationDirection);

		startActivity(intent);
	}

	private Uri getStationRoutesUri(long stationId) {
		return BusTimeContract.Stations.buildStationRoutesUri(stationId);
	}

	@Override
	protected void onPause() {
		super.onPause();

		BusProvider.getBus().unregister(this);
	}
}
