package app.android.bustime.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import app.android.bustime.R;
import app.android.bustime.db.DbImportException;
import app.android.bustime.db.DbImporter;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;


public class HomeActivity extends SherlockFragmentActivity
{
	private static final String SAVED_INSTANCE_KEY_SELECTED_TAB = "selected_tab";

	private RotationSafeTask<HomeActivity> checkDatabaseUpdateWithServerTask;
	private RotationSafeTask<HomeActivity> updateDatabaseWithServerTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setUpTabs();
		restorePreviousSelectedTab(savedInstanceState);

		checkDatabaseUpdates();

		restorePreviousRunTasks();
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
		checkDatabaseUpdateWithServerTask = new CheckDatabaseUpdateWithServerTask();
		checkDatabaseUpdateWithServerTask.setHostActivity(this);

		checkDatabaseUpdateWithServerTask.execute();
	}

	private static class CheckDatabaseUpdateWithServerTask extends RotationSafeTask<HomeActivity>
	{
		private boolean isLocalDatabaseEverUpdated;
		private boolean isLocalDatabaseUpdateAvailable;

		@Override
		protected void onBeforeExecution() {
			isLocalDatabaseEverUpdated = false;
			isLocalDatabaseUpdateAvailable = false;
		}

		@Override
		protected String doInBackground(Void... voids) {
			try {
				DbImporter dbImporter = new DbImporter(getHostActivity());

				isLocalDatabaseEverUpdated = dbImporter.isLocalDatabaseEverUpdated();

				if (!isLocalDatabaseEverUpdated) {
					return null;
				}

				isLocalDatabaseUpdateAvailable = dbImporter.isLocalDatabaseUpdateAvailable();
			}
			catch (DbImportException e) {
				// Skip exceptions, this update check is optional
			}

			return null;
		}

		@Override
		protected void onAfterExecution(String errorMessage) {
			if (!isLocalDatabaseEverUpdated) {
				getHostActivity().callDatabaseUpdating();

				return;
			}

			if (isLocalDatabaseUpdateAvailable) {
				getHostActivity().showUpdateAvailableSign();
			}
		}

		@Override
		protected void onSettingHostActivity() {
		}

		@Override
		protected void onResettingHostActivity() {
		}
	}

	private void callDatabaseUpdating() {
		updateDatabaseWithServerTask = new UpdateDatabaseWithServerTask();
		updateDatabaseWithServerTask.setHostActivity(this);

		updateDatabaseWithServerTask.execute();
	}

	private static class UpdateDatabaseWithServerTask extends RotationSafeTask<HomeActivity>
	{
		private ProgressDialogHelper progressDialogHelper;

		@Override
		protected void onBeforeExecution() {
			showProgressDialog();
		}

		private void showProgressDialog() {
			progressDialogHelper = new ProgressDialogHelper();
			progressDialogHelper.show(getHostActivity(), R.string.loading_update);
		}

		@Override
		protected String doInBackground(Void... parameters) {
			try {
				DbImporter dbImporter = new DbImporter(getHostActivity());
				dbImporter.importFromServer();
			}
			catch (DbImportException e) {
				return getHostActivity().getString(R.string.error_unspecified);
			}

			return new String();
		}

		@Override
		protected void onAfterExecution(String errorMessage) {
			if (TextUtils.isEmpty(errorMessage)) {
				getHostActivity().reSetUpTabs();
				getHostActivity().hideUpdateAvailableSign();
			}
			else {
				UserAlerter.alert(getHostActivity(), errorMessage);
			}

			hideProgressDialog();

			getHostActivity().updateDatabaseWithServerTask = null;
		}

		private void hideProgressDialog() {
			progressDialogHelper.hide();

			progressDialogHelper = null;
		}

		@Override
		protected void onResettingHostActivity() {
			hideProgressDialog();
		}

		@Override
		protected void onSettingHostActivity() {
			// Show progress dialog again only when it was shown before
			if (progressDialogHelper != null) {
				showProgressDialog();
			}
		}
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

	private void showUpdateAvailableSign() {
		getSupportActionBar().setSubtitle(R.string.warning_update_available);
	}

	private void restorePreviousRunTasks() {
		RotationHelper.setHostActivity(this, getLastCustomNonConfigurationInstance());
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

	@Override
	public Object onRetainCustomNonConfigurationInstance() {
		RotationHelper.resetHostActivity(updateDatabaseWithServerTask,
			checkDatabaseUpdateWithServerTask);

		return RotationHelper.buildRetainTasks(updateDatabaseWithServerTask,
			checkDatabaseUpdateWithServerTask);
	}
}
