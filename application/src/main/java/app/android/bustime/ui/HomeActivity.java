package app.android.bustime.ui;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import app.android.bustime.R;
import app.android.bustime.ui.loader.DatabaseUpdateCheckLoader;
import app.android.bustime.ui.loader.DatabaseUpdateLoader;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;


public class HomeActivity extends SherlockFragmentActivity
{
	private static final String SAVED_INSTANCE_KEY_SELECTED_TAB = "selected_tab";

	private static final int DATABASE_UPDATE_LOADER_ID = 2;
	private static final int DATABASE_UPDATE_CHECK_LOADER_ID = 3;

	private ProgressDialogHelper updateProgressDialogHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initRunningLoaders();

		setUpTabs();
		restorePreviousSelectedTab(savedInstanceState);

		checkDatabaseUpdates();

		showRunningUpdateProgressDialog();
	}

	private void initRunningLoaders() {
		LoaderManager loaderManager = getSupportLoaderManager();

		if (loaderManager.getLoader(DATABASE_UPDATE_LOADER_ID) != null) {
			loaderManager.initLoader(DATABASE_UPDATE_LOADER_ID, null, databaseUpdateCallback);
		}

		if (loaderManager.getLoader(DATABASE_UPDATE_CHECK_LOADER_ID) != null) {
			loaderManager.initLoader(DATABASE_UPDATE_CHECK_LOADER_ID, null, databaseUpdateCheckCallback);
		}
	}

	private void setUpTabs() {
		ActionBar actionBar = getSupportActionBar();

		actionBar.addTab(buildRoutesTab());
		actionBar.addTab(buildStationsTab());
	}

	private ActionBar.Tab buildRoutesTab() {
		ActionBar.Tab tab = getSupportActionBar().newTab();

		tab.setText(getString(R.string.title_routes));
		tab.setTabListener(new TabListener(FragmentFactory.createRoutesFragment(this)));

		return tab;
	}

	public static class TabListener implements ActionBar.TabListener
	{
		private final Fragment fragment;

		public TabListener(Fragment fragment) {
			this.fragment = fragment;
		}

		@Override
		public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
			if (fragment.isDetached()) {
				fragmentTransaction.attach(fragment);
			}
			else {
				fragmentTransaction.replace(android.R.id.content, fragment);
			}
		}

		@Override
		public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
			if (!fragment.isDetached()) {
				fragmentTransaction.detach(fragment);
			}
		}

		@Override
		public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		}
	}

	private ActionBar.Tab buildStationsTab() {
		ActionBar.Tab tab = getSupportActionBar().newTab();

		tab.setText(getString(R.string.title_stations));
		tab.setTabListener(new TabListener(FragmentFactory.createStationsFragment(this)));

		return tab;
	}

	private void restorePreviousSelectedTab(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			setSelectedTab(savedInstanceState.getInt(SAVED_INSTANCE_KEY_SELECTED_TAB, 0));
		}
	}

	private void setSelectedTab(int tabPosition) {
		getSupportActionBar().setSelectedNavigationItem(tabPosition);
	}

	private void checkDatabaseUpdates() {
		LoaderManager loaderManager = getSupportLoaderManager();

		loaderManager.initLoader(DATABASE_UPDATE_CHECK_LOADER_ID, null, databaseUpdateCheckCallback);
	}

	private final LoaderManager.LoaderCallbacks<Bundle> databaseUpdateCheckCallback = new LoaderManager.LoaderCallbacks<Bundle>()
	{
		@Override
		public Loader<Bundle> onCreateLoader(int i, Bundle bundle) {
			return new DatabaseUpdateCheckLoader(HomeActivity.this);
		}

		@Override
		public void onLoadFinished(Loader<Bundle> databaseUpdateCheckLoader, Bundle databaseUpdateCheckResult) {
			getSupportLoaderManager().destroyLoader(DATABASE_UPDATE_CHECK_LOADER_ID);

			boolean isDatabaseEverUpdated = databaseUpdateCheckResult.getBoolean(
				DatabaseUpdateCheckLoader.RESULT_DATABASE_EVER_UPDATED_KEY);

			if (!isDatabaseEverUpdated) {
				callDatabaseUpdating();

				return;
			}

			boolean isDatabaseUpdateAvailable = databaseUpdateCheckResult.getBoolean(
				DatabaseUpdateCheckLoader.RESULT_DATABASE_UPDATE_AVAILABLE_KEY);

			if (isDatabaseUpdateAvailable) {
				showMessageUpdateAvailable();
			}
		}

		@Override
		public void onLoaderReset(Loader<Bundle> bundleLoader) {
		}
	};

	private void callDatabaseUpdating() {
		getSupportLoaderManager().initLoader(DATABASE_UPDATE_LOADER_ID, null, databaseUpdateCallback);
	}

	private final LoaderManager.LoaderCallbacks<String> databaseUpdateCallback = new LoaderManager.LoaderCallbacks<String>()
	{
		@Override
		public Loader<String> onCreateLoader(int loaderId, Bundle loaderArguments) {
			showUpdateProgressDialog();

			return new DatabaseUpdateLoader(HomeActivity.this);
		}

		@Override
		public void onLoadFinished(Loader<String> databaseUpdateLoader, String errorMessage) {
			getSupportLoaderManager().destroyLoader(DATABASE_UPDATE_LOADER_ID);

			if (TextUtils.isEmpty(errorMessage)) {
				// We should put actions to handler to allow run fragment transaction in onLoadFinished
				Handler handler = new Handler();

				handler.post(new Runnable()
				{
					@Override
					public void run() {
						reSetUpTabs();
					}
				});

				hideUpdateAvailableSign();
			}
			else {
				UserAlerter.alert(HomeActivity.this, errorMessage);
			}

			hideUpdateProgressDialog();
		}

		@Override
		public void onLoaderReset(Loader<String> databaseUpdateLoader) {
		}
	};

	private void showUpdateProgressDialog() {
		updateProgressDialogHelper = new ProgressDialogHelper();
		updateProgressDialogHelper.show(this, R.string.loading_update);
	}

	private void reSetUpTabs() {
		int selectedTabPosition = getSelectedTabPosition();

		tearDownTabs();
		setUpTabs();

		setSelectedTab(selectedTabPosition);
	}

	private int getSelectedTabPosition() {
		return getSupportActionBar().getSelectedNavigationIndex();
	}

	private void tearDownTabs() {
		getSupportActionBar().removeAllTabs();
	}

	private void hideUpdateAvailableSign() {
		getSupportActionBar().setSubtitle(null);
	}

	private void hideUpdateProgressDialog() {
		if (updateProgressDialogHelper != null) {
			updateProgressDialogHelper.hide();
		}

		updateProgressDialogHelper = null;
	}

	private void showMessageUpdateAvailable() {
		getSupportActionBar().setSubtitle(R.string.warning_update_available);
	}

	private void showRunningUpdateProgressDialog() {
		if (getLastCustomNonConfigurationInstance() != null) {
			showUpdateProgressDialog();
		}
	}

	@Override
	public Object onRetainCustomNonConfigurationInstance() {
		return updateProgressDialogHelper;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.menu_action_bar_home, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case R.id.menu_update:
				callDatabaseUpdating();
				return true;

			case R.id.menu_map:
				callStationsMapActivity();
				return true;

			default:
				return super.onOptionsItemSelected(menuItem);
		}
	}

	private void callStationsMapActivity() {
		Intent callIntent = IntentFactory.createStationsMapIntent(this);
		startActivity(callIntent);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putInt(SAVED_INSTANCE_KEY_SELECTED_TAB, getSelectedTabPosition());
	}
}
