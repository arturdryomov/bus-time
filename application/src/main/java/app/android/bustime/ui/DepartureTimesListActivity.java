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
import app.android.bustime.local.Route;
import app.android.bustime.local.Time;


public class DepartureTimesListActivity extends SimpleAdapterListActivity
{
	private final Context activityContext = this;

	private Route route;

	private static final String LIST_ITEM_TEXT_ID = "text";
	private static final String LIST_ITEM_OBJECT_ID = "object";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.departure_times);

		initializeActionbar();
		initializeList();

		processReceivedRoute();
	}

	private void initializeActionbar() {
		ImageButton itemCreationButton = (ImageButton) findViewById(R.id.itemCreationButton);
		itemCreationButton.setOnClickListener(departureTimeCreationListener);
	}

	private final OnClickListener departureTimeCreationListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			callDepartureTimeCreation();
		}

		private void callDepartureTimeCreation() {
			Intent callIntent = IntentFactory.createDepartureTimeCreationIntent(activityContext, route);
			activityContext.startActivity(callIntent);
		}
	};

	@Override
	protected void initializeList() {
		SimpleAdapter departureTimesAdapter = new SimpleAdapter(activityContext, listData,
			R.layout.departure_times_list_item, new String[] { LIST_ITEM_TEXT_ID },
			new int[] { R.id.text });

		setListAdapter(departureTimesAdapter);

		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		registerForContextMenu(getListView());
	}

	private void processReceivedRoute() {
		Bundle receivedData = this.getIntent().getExtras();

		if (receivedData.containsKey(IntentFactory.MESSAGE_ID)) {
			route = receivedData.getParcelable(IntentFactory.MESSAGE_ID);
		}
		else {
			UserAlerter.alert(activityContext, getString(R.string.someError));

			finish();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		loadDepartureTimes();
	}

	private void loadDepartureTimes() {
		new LoadDepartureTimesTask().execute();
	}

	private class LoadDepartureTimesTask extends AsyncTask<Void, Void, String>
	{
		private List<Time> departureTimes;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			setEmptyListText(getString(R.string.loadingDepartureTimes));
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				departureTimes = route.getDepartureTimetable();
			}
			catch (DbException e) {
				return getString(R.string.someError);
			}

			return new String();
		}

		@Override
		protected void onPostExecute(String errorMessage) {
			super.onPostExecute(errorMessage);

			if (departureTimes.isEmpty()) {
				setEmptyListText(getString(R.string.noDepartureTimes));
			}
			else {
				fillList(departureTimes);
				updateList();
			}

			if (!errorMessage.isEmpty()) {
				UserAlerter.alert(activityContext, errorMessage);
			}
		}
	}

	@Override
	protected void addItemToList(Object itemData) {
		Time time = (Time) itemData;

		HashMap<String, Object> timeItem = new HashMap<String, Object>();

		timeItem.put(LIST_ITEM_TEXT_ID, time.toString());
		timeItem.put(LIST_ITEM_OBJECT_ID, time);

		listData.add(timeItem);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);

		getMenuInflater().inflate(R.menu.departure_times_context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo itemInfo = (AdapterContextMenuInfo) item.getMenuInfo();
		int timePosition = itemInfo.position;

		switch (item.getItemId()) {
			case R.id.edit:
				callDepartureTimeEditing(timePosition);
				return true;
			case R.id.delete:
				callDepartureTimeDeleting(timePosition);
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}

	private void callDepartureTimeEditing(int timePosition) {
		Time time = getTime(timePosition);

		// TODO: Call editing with intent
	}

	private void callDepartureTimeDeleting(int timePosition) {
		new DeleteDepartureTimeTask(timePosition).execute();
	}

	private class DeleteDepartureTimeTask extends AsyncTask<Void, Void, String>
	{
		private final int timePosition;
		private final Time time;

		public DeleteDepartureTimeTask(int timePosition) {
			super();

			this.timePosition = timePosition;
			this.time = getTime(timePosition);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			listData.remove(timePosition);
			updateList();

			if (listData.isEmpty()) {
				setEmptyListText(getString(R.string.noDepartureTimes));
			}
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				route.removeDepartureTime(time);
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

	private Time getTime(int timePosition) {
		SimpleAdapter listAdapter = (SimpleAdapter) getListAdapter();

		@SuppressWarnings("unchecked")
		Map<String, Object> adapterItem = (Map<String, Object>) listAdapter.getItem(timePosition);

		return (Time) adapterItem.get(LIST_ITEM_OBJECT_ID);
	}
}
