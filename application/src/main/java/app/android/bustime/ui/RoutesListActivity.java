package app.android.bustime.ui;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import app.android.bustime.R;
import app.android.bustime.local.DbProvider;
import app.android.bustime.local.Route;


public class RoutesListActivity extends SimpleAdapterListActivity
{
	private final Context activityContext = this;

	private static final String LIST_ITEM_TEXT_ID = "text";
	private static final String LIST_ITEM_OBJECT_ID = "object";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_routes);

		initializeActionbar();
		initializeList();
	}

	private void initializeActionbar() {
		ImageButton itemCreationButton = (ImageButton) findViewById(R.id.item_creation_button);
		itemCreationButton.setOnClickListener(routeCreationListener);
	}

	private final OnClickListener routeCreationListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			callRouteCreation();
		}

		private void callRouteCreation() {
			Intent callIntent = IntentFactory.createRouteCreationIntent(activityContext);
			activityContext.startActivity(callIntent);
		}
	};

	@Override
	protected void initializeList() {
		SimpleAdapter routesAdapter = new SimpleAdapter(activityContext, listData,
			R.layout.list_item_one_line, new String[] { LIST_ITEM_TEXT_ID }, new int[] { R.id.text });

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

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);

		getMenuInflater().inflate(R.menu.routes_context_menu_items, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo itemInfo = (AdapterContextMenuInfo) item.getMenuInfo();
		int routePosition = itemInfo.position;

		switch (item.getItemId()) {
			case R.id.rename:
				callRouteRenaming(routePosition);
				return true;
			case R.id.delete:
				callRouteDeleting(routePosition);
				return true;
			case R.id.editDepartureTimetable:
				callDepartureTimesList(routePosition);
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}

	private void callRouteRenaming(int routePosition) {
		Route route = getRoute(routePosition);

		Intent callIntent = IntentFactory.createRouteRenamingIntent(activityContext, route);
		startActivity(callIntent);
	}

	private Route getRoute(int routePosition) {
		SimpleAdapter routesAdapter = (SimpleAdapter) getListAdapter();

		@SuppressWarnings("unchecked")
		Map<String, Object> adapterItem = (Map<String, Object>) routesAdapter.getItem(routePosition);

		return (Route) adapterItem.get(LIST_ITEM_OBJECT_ID);
	}

	private void callRouteDeleting(int routePosition) {
		new DeleteRouteTask(routePosition).execute();
	}

	private class DeleteRouteTask extends AsyncTask<Void, Void, Void>
	{
		private final int routePosition;
		private final Route route;

		public DeleteRouteTask(int routePosition) {
			super();

			this.routePosition = routePosition;
			this.route = getRoute(routePosition);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			listData.remove(routePosition);
			updateList();

			if (listData.isEmpty()) {
				setEmptyListText(getString(R.string.empty_routes));
			}
		}

		@Override
		protected Void doInBackground(Void... params) {
			DbProvider.getInstance().getRoutes().deleteRoute(route);

			return null;
		}
	}

	private void callDepartureTimesList(int routePosition) {
		Route route = getRoute(routePosition);

		Intent callIntent = IntentFactory.createDepartureTimetableIntent(activityContext, route);
		startActivity(callIntent);
	}

	@Override
	protected void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);

		callStationsList(position);
	}

	private void callStationsList(int routePosition) {
		Route route = getRoute(routePosition);

		Intent callIntent = IntentFactory.createStationsListIntent(activityContext, route);
		startActivity(callIntent);
	}
}