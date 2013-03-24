package ru.ming13.bustime.ui.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import ru.ming13.bustime.R;
import ru.ming13.bustime.db.model.Route;
import ru.ming13.bustime.ui.fragment.StationsFragment;
import ru.ming13.bustime.ui.intent.IntentException;
import ru.ming13.bustime.ui.intent.IntentExtras;
import ru.ming13.bustime.ui.util.FragmentWrapper;
import ru.ming13.bustime.ui.util.NameParser;


public class StationsActivity extends SherlockFragmentActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FragmentWrapper.setUpFragment(this, buildFragment());

		setUpActionBarTitle();
	}

	private Fragment buildFragment() {
		return StationsFragment.newForRouteSortedByTimeShiftLoadingInstance(extractReceivedRoute());
	}

	private Route extractReceivedRoute() {
		Route route = getIntent().getParcelableExtra(IntentExtras.ROUTE);

		if (route == null) {
			throw new IntentException();
		}

		return route;
	}

	private void setUpActionBarTitle() {
		String routeNumber = NameParser.parseRouteNumber(extractReceivedRoute().getName());

		getSupportActionBar().setTitle(routeNumber);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.menu_action_bar_stations, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		if (menuItem.isChecked()) {
			return false;
		}

		menuItem.setChecked(true);

		StationsFragment stationsFragment = (StationsFragment) getSupportFragmentManager().findFragmentById(
			android.R.id.content);

		switch (menuItem.getItemId()) {
			case R.id.menu_stations_order_name:
				stationsFragment.sortByName();
				return true;

			case R.id.menu_stations_order_route:
				stationsFragment.sortByTimeShift();
				return true;

			default:
				return super.onOptionsItemSelected(menuItem);
		}
	}
}
