package ru.ming13.bustime.ui.fragment;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import ru.ming13.bustime.R;
import ru.ming13.bustime.db.model.Route;
import ru.ming13.bustime.db.model.Station;
import ru.ming13.bustime.ui.intent.IntentFactory;
import ru.ming13.bustime.ui.loader.Loaders;
import ru.ming13.bustime.ui.loader.RoutesLoader;


public class RoutesFragment extends AdaptedListFragment<Route> implements LoaderManager.LoaderCallbacks<List<Route>>
{
	private static final String LIST_ITEM_TEXT_ID = "text";

	private static enum Mode
	{
		ALL, FOR_STATION
	}

	private Mode mode;

	private Station station;

	public static RoutesFragment newInstance() {
		RoutesFragment routesFragment = new RoutesFragment();

		routesFragment.mode = Mode.ALL;

		return routesFragment;
	}

	public static RoutesFragment newInstance(Station station) {
		RoutesFragment routesFragment = new RoutesFragment();

		routesFragment.mode = Mode.FOR_STATION;
		routesFragment.station = station;

		return routesFragment;
	}

	@Override
	protected SimpleAdapter buildListAdapter() {
		return new SimpleAdapter(getActivity(), list, R.layout.list_item_one_line,
			new String[] {LIST_ITEM_TEXT_ID}, new int[] {R.id.text});
	}

	@Override
	protected Map<String, Object> buildListItem(Route route) {
		Map<String, Object> listItem = new HashMap<String, Object>();

		listItem.put(LIST_ITEM_OBJECT_ID, route);
		listItem.put(LIST_ITEM_TEXT_ID, route.getName());

		return listItem;
	}

	@Override
	protected void callListPopulation() {
		setEmptyListText(R.string.loading_routes);

		getLoaderManager().initLoader(Loaders.ROUTES, null, this);
	}

	@Override
	public Loader<List<Route>> onCreateLoader(int loaderId, Bundle loaderArguments) {
		switch (mode) {
			case ALL:
				return new RoutesLoader(getActivity());

			case FOR_STATION:
				return new RoutesLoader(getActivity(), station);

			default:
				return new RoutesLoader(getActivity());
		}
	}

	@Override
	public void onLoadFinished(Loader<List<Route>> routesLoader, List<Route> routes) {
		if (routes.isEmpty()) {
			setEmptyListText(R.string.empty_routes);
		}
		else {
			populateList(routes);
		}
	}

	@Override
	public void onLoaderReset(Loader<List<Route>> routesLoader) {
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);

		Route selectedRoute = getListItemObject(position);

		switch (mode) {
			case ALL:
				callStationsActivity(selectedRoute);
				return;

			case FOR_STATION:
				callTimetableActivity(selectedRoute);
		}
	}

	private void callTimetableActivity(Route route) {
		Intent callIntent = IntentFactory.createTimetableIntent(getActivity(), route, station);
		startActivity(callIntent);
	}

	private void callStationsActivity(Route route) {
		Intent callIntent = IntentFactory.createStationsIntent(getActivity(), route);
		startActivity(callIntent);
	}
}
