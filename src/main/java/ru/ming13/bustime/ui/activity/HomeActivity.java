package ru.ming13.bustime.ui.activity;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.squareup.otto.Subscribe;
import ru.ming13.bustime.R;
import ru.ming13.bustime.ui.bus.BusEventsCollector;
import ru.ming13.bustime.ui.bus.BusProvider;
import ru.ming13.bustime.ui.bus.DatabaseUpdateAvailableEvent;
import ru.ming13.bustime.ui.bus.DatabaseUpdateFailedEvent;
import ru.ming13.bustime.ui.bus.DatabaseUpdateSucceedEvent;
import ru.ming13.bustime.ui.bus.NoDatabaseUpdatesEverEvent;
import ru.ming13.bustime.ui.fragment.ProgressDialogFragment;
import ru.ming13.bustime.ui.fragment.RoutesFragment;
import ru.ming13.bustime.ui.fragment.StationsFragment;
import ru.ming13.bustime.ui.intent.IntentFactory;
import ru.ming13.bustime.ui.task.DatabaseUpdateCheckTask;
import ru.ming13.bustime.ui.task.DatabaseUpdateTask;
import ru.ming13.bustime.ui.util.ActionBarTabListener;
import ru.ming13.bustime.ui.util.Preferences;
import ru.ming13.bustime.ui.util.UserAlerter;


public class HomeActivity extends ActionBarActivity
{
	private static enum Mode
	{
		DEFAULT, UPDATE_AVAILABLE
	}

	private Mode mode = Mode.DEFAULT;

	private RoutesFragment routesFragment;
	private StationsFragment stationsFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setUpTabs();
		selectLastTimeSelectedTab();

		checkDatabaseUpdates();
	}

	private void setUpTabs() {
		ActionBar actionBar = getSupportActionBar();

		actionBar.addTab(buildRoutesTab());
		actionBar.addTab(buildStationsTab());
	}

	private ActionBar.Tab buildRoutesTab() {
		ActionBar.Tab tab = getSupportActionBar().newTab();

		routesFragment = RoutesFragment.newInstance();

		tab.setText(R.string.title_routes);
		tab.setTabListener(new ActionBarTabListener(routesFragment));

		return tab;
	}

	private ActionBar.Tab buildStationsTab() {
		ActionBar.Tab tab = getSupportActionBar().newTab();

		stationsFragment = StationsFragment.newAllLoadingInstance();

		tab.setText(R.string.title_stations);
		tab.setTabListener(new ActionBarTabListener(stationsFragment));

		return tab;
	}

	private void selectLastTimeSelectedTab() {
		int selectedTabIndex = Preferences.getInt(this, Preferences.Keys.SELECTED_TAB_INDEX);

		getSupportActionBar().setSelectedNavigationItem(selectedTabIndex);
	}

	private void checkDatabaseUpdates() {
		DatabaseUpdateCheckTask.execute(this);
	}

	@Subscribe
	public void onDatabaseUpdateAvailable(DatabaseUpdateAvailableEvent event) {
		mode = Mode.UPDATE_AVAILABLE;

		setUpUpdatingAvailableMessage();
		setUpActionBarButtons();
	}

	private void setUpUpdatingAvailableMessage() {
		switch (mode) {
			case UPDATE_AVAILABLE:
				getSupportActionBar().setSubtitle(R.string.warning_update_available);
				break;

			default:
				getSupportActionBar().setSubtitle(null);
				break;
		}
	}

	private void setUpActionBarButtons() {
		supportInvalidateOptionsMenu();
	}

	@Subscribe
	public void onNoDatabaseUpdatesEver(NoDatabaseUpdatesEverEvent event) {
		updateDatabase();
	}

	private void updateDatabase() {
		showUpdatingProgressDialog();

		DatabaseUpdateTask.execute(this);
	}

	private void showUpdatingProgressDialog() {
		String dialogMessage = getString(R.string.loading_update);
		ProgressDialogFragment progressDialog = ProgressDialogFragment.newInstance(dialogMessage);

		progressDialog.show(getSupportFragmentManager(), ProgressDialogFragment.TAG);
	}

	@Subscribe
	public void onDatabaseUpdateSucceed(DatabaseUpdateSucceedEvent event) {
		mode = Mode.DEFAULT;

		refreshFragments();

		setUpUpdatingAvailableMessage();
		setUpActionBarButtons();

		hideUpdatingProgressDialog();
	}

	private void refreshFragments() {
		if (routesFragment.isAdded()) {
			routesFragment.callListRepopulation();
		}

		if (stationsFragment.isAdded()) {
			stationsFragment.callListRepopulation();
		}
	}

	private void hideUpdatingProgressDialog() {
		ProgressDialogFragment progressDialog = (ProgressDialogFragment) getSupportFragmentManager().findFragmentByTag(
			ProgressDialogFragment.TAG);

		if (progressDialog == null) {
			return;
		}

		progressDialog.dismiss();
	}

	@Subscribe
	public void onDatabaseUpdateFailed(DatabaseUpdateFailedEvent event) {
		hideUpdatingProgressDialog();

		if (event.isNetworkRelatedProblem()) {
			UserAlerter.alert(this, R.string.error_connection);
		}
		else {
			UserAlerter.alert(this, R.string.error_unspecified);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_action_bar_home, menu);

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem updatingActionBarButton = menu.findItem(R.id.menu_update_information);
		MenuItem searchActionBarButton = menu.findItem(R.id.menu_search_stations);

		switch (mode) {
			case UPDATE_AVAILABLE:
				updatingActionBarButton.setVisible(true);
				MenuItemCompat.setShowAsAction(searchActionBarButton, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
				break;

			default:
				updatingActionBarButton.setVisible(false);
				MenuItemCompat.setShowAsAction(searchActionBarButton, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
				break;
		}

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case R.id.menu_search_stations:
				callStationsSearch();
				return true;

			case R.id.menu_update_information:
				updateDatabase();
				return true;

			case R.id.menu_rate_application:
				callGooglePlay();
				return true;

			case R.id.menu_send_feedback:
				callSendingFeedback();
				return true;

			default:
				return super.onOptionsItemSelected(menuItem);
		}
	}

	private void callStationsSearch() {
		onSearchRequested();
	}

	private void callGooglePlay() {
		try {
			Intent intent = IntentFactory.createGooglePlayIntent(buildAppGooglePlayUrl());
			startActivity(intent);
		}
		catch (ActivityNotFoundException e) {
			Intent intent = IntentFactory.createGooglePlayIntent(buildWebGooglePlayUrl());
			startActivity(intent);
		}
	}

	private String buildAppGooglePlayUrl() {
		return getString(R.string.url_app_google_play, getPackageName());
	}

	private String buildWebGooglePlayUrl() {
		return getString(R.string.url_web_google_play, getPackageName());
	}

	private void callSendingFeedback() {
		Intent intent = IntentFactory.createEmailIntent(getString(R.string.email_address_feedback),
			getString(R.string.email_subject_feedback));
		startActivity(Intent.createChooser(intent, null));
	}

	@Override
	protected void onStop() {
		super.onStop();

		saveSelectedTab();
	}

	private void saveSelectedTab() {
		int selectedTabIndex = getSupportActionBar().getSelectedNavigationIndex();

		Preferences.set(this, Preferences.Keys.SELECTED_TAB_INDEX, selectedTabIndex);
	}

	@Override
	protected void onResume() {
		super.onResume();

		BusProvider.getInstance().register(this);

		BusEventsCollector.getInstance().postCollectedEvents();
	}

	@Override
	protected void onPause() {
		super.onPause();

		BusProvider.getInstance().unregister(this);
	}
}
