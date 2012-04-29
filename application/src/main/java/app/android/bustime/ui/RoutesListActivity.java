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
import app.android.bustime.local.DbException;
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
		setContentView(R.layout.routes);

		initializeActionbar();
		initializeList();
	}

	private void initializeActionbar() {
		ImageButton itemCreationButton = (ImageButton) findViewById(R.id.itemCreationButton);
		itemCreationButton.setOnClickListener(routeCreationListener);
	}

	private final OnClickListener routeCreationListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			callRouteCreation();
		}

		private void callRouteCreation() {
			Intent callIntent = IntentFactory.createDeckCreationIntent(activityContext);
			activityContext.startActivity(callIntent);
		}
	};

	@Override
	protected void initializeList() {
		SimpleAdapter routesAdapter = new SimpleAdapter(activityContext, listData,
			R.layout.routes_list_item, new String[] { LIST_ITEM_TEXT_ID }, new int[] { R.id.text });

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

	private class LoadRoutesTask extends AsyncTask<Void, Void, String>
	{
		private List<Route> routes;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			setEmptyListText(getString(R.string.loadingRoutes));
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				routes = DbProvider.getInstance().getRoutes().getRoutesList();
			}
			catch (DbException e) {
				return getString(R.string.someError);
			}

			return new String();
		}

		@Override
		protected void onPostExecute(String errorMessage) {
			super.onPostExecute(errorMessage);

			if (routes.isEmpty()) {
				setEmptyListText(getString(R.string.noRoutes));
			}
			else {
				fillList(routes);
				updateList();
			}

			if (!errorMessage.isEmpty()) {
				UserAlerter.alert(activityContext, errorMessage);
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

		getMenuInflater().inflate(R.menu.routes_context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo itemInfo = (AdapterContextMenuInfo) item.getMenuInfo();
		int routePosition = itemInfo.position;

		switch (item.getItemId()) {
			case R.id.rename:
				// TODO: callRouteRenaming(routePosition);
				return true;
			case R.id.delete:
				callRouteDeleting(routePosition);
				return true;
			case R.id.editDepartureTimetable:
				// TODO: callDepartureTimetable(routePosition);
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}

	private void callRouteDeleting(int routePosition) {
		new DeleteRouteTask(routePosition).execute();
	}

	private class DeleteRouteTask extends AsyncTask<Void, Void, String>
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
				setEmptyListText(getString(R.string.noRoutes));
			}
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				DbProvider.getInstance().getRoutes().deleteRoute(route);
			}
			catch (DbException e) {
				return getString(R.string.someError);
			}

			return new String();
		}

		@Override
		protected void onPostExecute(String errorMessage) {
			super.onPostExecute(errorMessage);

			if (!errorMessage.isEmpty()) {
				UserAlerter.alert(activityContext, errorMessage);
			}
		}
	}

	private Route getRoute(int routePosition) {
		SimpleAdapter listAdapter = (SimpleAdapter) getListAdapter();

		@SuppressWarnings("unchecked")
		Map<String, Object> adapterItem = (Map<String, Object>) listAdapter.getItem(routePosition);

		return (Route) adapterItem.get(LIST_ITEM_OBJECT_ID);
	}
}
