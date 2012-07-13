package app.android.bustime.ui.dispatch.routes;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import app.android.bustime.R;
import app.android.bustime.db.DbProvider;
import app.android.bustime.db.Route;
import app.android.bustime.ui.SimpleAdapterListActivity;


public class RoutesListActivity extends SimpleAdapterListActivity
{
	private final Context activityContext = this;

	private static final String LIST_ITEM_TEXT_ID = "text";
	private static final String LIST_ITEM_OBJECT_ID = "object";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);

		initializeList();
	}

	@Override
	protected void initializeList() {
		SimpleAdapter routesAdapter = new SimpleAdapter(activityContext, listData,
			R.layout.list_item_one_line, new String[] {LIST_ITEM_TEXT_ID}, new int[] {R.id.text});

		setListAdapter(routesAdapter);

		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		registerForContextMenu(getListView());
	}

	@Override
	protected void onResume() {
		super.onResume();

		loadRoutes();
	}

	private void loadRoutes() {
		new LoadRoutesTask().execute();
	}

	private class LoadRoutesTask extends AsyncTask<Void, Void, Void>
	{
		private List<Route> routesList;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			setEmptyListText(getString(R.string.loading_routes));
		}

		@Override
		protected Void doInBackground(Void... params) {
			routesList = DbProvider.getInstance().getRoutes().getRoutesList();

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (routesList.isEmpty()) {
				setEmptyListText(getString(R.string.empty_routes));
			}
			else {
				fillList(routesList);
			}
		}
	}

	@Override
	protected void addItemToList(Object itemData) {
		Route route = (Route) itemData;

		HashMap<String, Object> routeItem = new HashMap<String, Object>();

		routeItem.put(LIST_ITEM_TEXT_ID, route.getName());
		routeItem.put(LIST_ITEM_OBJECT_ID, route);

		listData.add(routeItem);
	}

	private Route getRoute(int routePosition) {
		SimpleAdapter routesAdapter = (SimpleAdapter) getListAdapter();

		@SuppressWarnings(
			"unchecked") Map<String, Object> adapterItem = (Map<String, Object>) routesAdapter.getItem(
			routePosition);

		return (Route) adapterItem.get(LIST_ITEM_OBJECT_ID);
	}

	@Override
	protected void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);

		callStationsList(position);
	}

	private void callStationsList(int routePosition) {
		Route route = getRoute(routePosition);

		Intent callIntent = DispatchRoutesIntentFactory.createStationsListIntent(activityContext,
			route);
		startActivity(callIntent);
	}
}