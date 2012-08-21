package ru.ming13.bustime.ui.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import ru.ming13.bustime.R;
import ru.ming13.bustime.ui.fragment.IntermediateProgressDialog;
import ru.ming13.bustime.ui.fragment.RoutesFragment;
import ru.ming13.bustime.ui.fragment.StationsFragment;
import ru.ming13.bustime.ui.intent.IntentFactory;


public class HomeActivity extends SherlockFragmentActivity
{
	private static final String SAVED_INSTANCE_KEY_SELECTED_TAB = "selected_tab";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setUpTabs();
		restorePreviousStateSelectedTab(savedInstanceState);
	}

	private void setUpTabs() {
		ActionBar actionBar = getSupportActionBar();

		actionBar.addTab(buildRoutesTab());
		actionBar.addTab(buildStationsTab());
	}

	private ActionBar.Tab buildRoutesTab() {
		ActionBar.Tab tab = getSupportActionBar().newTab();

		tab.setText(R.string.title_routes);
		tab.setTabListener(new TabListener(RoutesFragment.newInstance()));

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

		tab.setText(R.string.title_stations);
		tab.setTabListener(new TabListener(StationsFragment.newInstance()));

		return tab;
	}

	private void restorePreviousStateSelectedTab(Bundle savedInstanceState) {
		if (isSavedInstanceValid(savedInstanceState)) {
			setSelectedTab(getPreviousStateSelectedTab(savedInstanceState));
		}
	}

	private boolean isSavedInstanceValid(Bundle savedInstanceState) {
		return savedInstanceState != null;
	}

	private int getPreviousStateSelectedTab(Bundle savedInstanceState) {
		return savedInstanceState.getInt(SAVED_INSTANCE_KEY_SELECTED_TAB, 0);
	}

	private void setSelectedTab(int tabPosition) {
		getSupportActionBar().setSelectedNavigationItem(tabPosition);
	}

	private void showUpdatingAvailableMessage() {
		getSupportActionBar().setSubtitle(R.string.warning_update_available);
	}

	private void hideUpdatingAvailableMessage() {
		getSupportActionBar().setSubtitle(null);
	}

	private void showUpdatingProgressDialog() {
		IntermediateProgressDialog progressDialog = IntermediateProgressDialog.newInstance(
			getString(R.string.loading_update));

		progressDialog.show(getSupportFragmentManager(), IntermediateProgressDialog.TAG);
	}

	private void hideUpdatingProgressDialog() {
		IntermediateProgressDialog intermediateProgressDialog = (IntermediateProgressDialog)
			getSupportFragmentManager().findFragmentByTag(IntermediateProgressDialog.TAG);

		if (intermediateProgressDialog != null) {
			intermediateProgressDialog.dismiss();
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.menu_action_bar_home, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case R.id.menu_search:
				callStationsSearch();
				return true;

			case R.id.menu_update:
				return true;

			case R.id.menu_map:
				callStationsMapActivity();
				return true;

			default:
				return super.onOptionsItemSelected(menuItem);
		}
	}

	private void callStationsSearch() {
		onSearchRequested();
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
