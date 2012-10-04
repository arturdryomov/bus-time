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
import ru.ming13.bustime.ui.intent.IntentFactory;
import ru.ming13.bustime.ui.loader.Loaders;
import ru.ming13.bustime.ui.loader.RoutesLoader;


public class RoutesFragment extends AdaptedListFragment<Route> implements LoaderManager.LoaderCallbacks<List<Route>>
{
	private static final String LIST_ITEM_TEXT_ID = "text";

	public static RoutesFragment newInstance() {
		return new RoutesFragment();
	}

	@Override
	protected SimpleAdapter buildListAdapter() {
		String[] listColumnNames = {LIST_ITEM_TEXT_ID};
		int[] columnCorrespondingResources = {R.id.text};

		return new SimpleAdapter(getActivity(), list, R.layout.list_item_one_line, listColumnNames,
			columnCorrespondingResources);
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
		return new RoutesLoader(getActivity());
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
	public void callListRepopulation() {
		setEmptyListText(R.string.loading_routes);
		clearList();

		getLoaderManager().restartLoader(Loaders.ROUTES, null, this);
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);

		Route selectedRoute = getListItemObject(position);
		callStationsActivity(selectedRoute);
	}

	private void callStationsActivity(Route route) {
		Intent intent = IntentFactory.createStationsIntent(getActivity(), route);
		startActivity(intent);
	}
}
