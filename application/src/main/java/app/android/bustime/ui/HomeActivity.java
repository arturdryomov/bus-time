package app.android.bustime.ui;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import app.android.bustime.R;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;


public class HomeActivity extends SherlockFragmentActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setUpActionBar();
		setUpTabs();
	}

	private void setUpActionBar() {
		// TODO: Move to xls

		ActionBar actionBar = getSupportActionBar();

		actionBar.setDisplayShowHomeEnabled(true);

		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	}

	private void setUpTabs() {
		ActionBar actionBar = getSupportActionBar();

		actionBar.addTab(buildRoutesTab());
		actionBar.addTab(buildStationsTab());
	}

	private ActionBar.Tab buildRoutesTab() {
		ActionBar.Tab tab = getSupportActionBar().newTab();

		tab.setText(getString(R.string.title_routes));
		tab.setTabListener(new TabListener<RoutesFragment>(this, RoutesFragment.class));

		return tab;
	}

	private ActionBar.Tab buildStationsTab() {
		ActionBar.Tab tab = getSupportActionBar().newTab();

		tab.setText(getString(R.string.title_stations));
		tab.setTabListener(new TabListener<StationsFragment>(this, StationsFragment.class));

		return tab;
	}

	public static class TabListener<T extends Fragment> implements ActionBar.TabListener
	{
		private final Activity activity;
		private final Class<T> fragmentClass;

		private Fragment fragment;

		public TabListener(Activity activity, Class<T> fragmentClass) {
			this.activity = activity;
			this.fragmentClass = fragmentClass;
		}

		@Override
		public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
			if (fragment == null) {
				fragment = Fragment.instantiate(activity, fragmentClass.getName());
				fragmentTransaction.replace(android.R.id.content, fragment);
			}
			else {
				fragmentTransaction.attach(fragment);
			}
		}

		@Override
		public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
			if (fragment != null) {
				fragmentTransaction.detach(fragment);
			}
		}

		@Override
		public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		}
	}
}
