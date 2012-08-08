package app.android.bustime.ui;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import app.android.bustime.R;
import app.android.bustime.db.DbProvider;
import app.android.bustime.db.Route;
import app.android.bustime.db.Station;


public class RoutesFragment extends AdaptedListFragment implements LoaderManager.LoaderCallbacks<List<Route>>
{
	private static final String LIST_ITEM_TEXT_ID = "text";

	private static final int ROUTES_LOADER_ID = 1;

	@Override
	protected SimpleAdapter buildListAdapter() {
		return new SimpleAdapter(getActivity(), list, R.layout.list_item_one_line,
			new String[] {LIST_ITEM_TEXT_ID}, new int[] {R.id.text});
	}

	@Override
	protected Map<String, Object> buildListItem(Object itemObject) {
		Route route = (Route) itemObject;

		Map<String, Object> listItem = new HashMap<String, Object>();

		listItem.put(LIST_ITEM_OBJECT_ID, route);
		listItem.put(LIST_ITEM_TEXT_ID, route.getName());

		return listItem;
	}

	@Override
	protected void callListPopulation() {
		setEmptyListText(getString(R.string.loading_routes));

		getLoaderManager().initLoader(ROUTES_LOADER_ID, getArguments(), this);
	}

	@Override
	public Loader<List<Route>> onCreateLoader(int loaderId, Bundle loaderArguments) {
		switch (loaderId) {
			case ROUTES_LOADER_ID:
				return new RoutesLoader(getActivity(), loaderArguments);

			default:
				throw new LoaderException();
		}
	}

	private static class RoutesLoader extends AsyncTaskLoader<List<Route>>
	{
		private final Bundle fragmentArguments;

		public RoutesLoader(Context context, Bundle fragmentArguments) {
			super(context);

			this.fragmentArguments = fragmentArguments;
		}

		@Override
		protected void onStartLoading() {
			super.onStartLoading();

			forceLoad();
		}

		@Override
		public List<Route> loadInBackground() {
			if (FragmentProcessor.haveMessage(fragmentArguments)) {
				Station station = (Station) FragmentProcessor.extractMessage(fragmentArguments);

				return DbProvider.getInstance().getRoutes().getRoutesList(station);
			}
			else {
				return DbProvider.getInstance().getRoutes().getRoutesList();
			}
		}
	}

	@Override
	public void onLoadFinished(Loader<List<Route>> routesLoader, List<Route> routes) {
		if (routes.isEmpty()) {
			setEmptyListText(getString(R.string.empty_routes));
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

		Route selectedRoute = (Route) getListItemObject(position);

		if (FragmentProcessor.haveMessage(getArguments())) {
			callTimetableActivity(selectedRoute);
		}
		else {
			callStationsActivity(selectedRoute);
		}
	}

	private void callTimetableActivity(Route route) {
		Station station = (Station) FragmentProcessor.extractMessage(getArguments());

		Intent callIntent = IntentFactory.createTimetableIntent(getActivity(), route, station);
		startActivity(callIntent);
	}

	private void callStationsActivity(Route route) {
		Intent callIntent = IntentFactory.createStationsIntent(getActivity(), route);
		startActivity(callIntent);
	}
}
