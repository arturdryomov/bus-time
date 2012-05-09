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
import app.android.bustime.local.Route;
import app.android.bustime.local.Time;


public class DepartureTimetableActivity extends SimpleAdapterListActivity
{
	private final Context activityContext = this;

	private Route route;

	private static final String LIST_ITEM_TEXT_ID = "text";
	private static final String LIST_ITEM_OBJECT_ID = "object";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_departure_timetable);

		processReceivedRoute();

		initializeActionbar();
		initializeList();
	}

	private void processReceivedRoute() {
		Bundle receivedData = this.getIntent().getExtras();

		if (receivedData.containsKey(IntentFactory.MESSAGE_ID)) {
			route = receivedData.getParcelable(IntentFactory.MESSAGE_ID);
		}
		else {
			UserAlerter.alert(activityContext, getString(R.string.error_unspecified));

			finish();
		}
	}

	private void initializeActionbar() {
		ImageButton itemCreationButton = (ImageButton) findViewById(R.id.item_creation_button);
		itemCreationButton.setOnClickListener(departureTimeCreationListener);
	}

	private final OnClickListener departureTimeCreationListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
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
			R.layout.list_item_one_line, new String[] { LIST_ITEM_TEXT_ID }, new int[] { R.id.text });

		setListAdapter(departureTimesAdapter);

		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		registerForContextMenu(getListView());
	}

	@Override
	protected void onResume() {
		super.onResume();

		loadDepartureTimetable();
	}

	private void loadDepartureTimetable() {
		new LoadDepartureTimetableTask().execute();
	}

	private class LoadDepartureTimetableTask extends AsyncTask<Void, Void, Void>
	{
		private List<Time> departureTimetable;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			setEmptyListText(getString(R.string.loading_departure_timetable));
		}

		@Override
		protected Void doInBackground(Void... params) {
			departureTimetable = route.getDepartureTimetable();

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (departureTimetable.isEmpty()) {
				setEmptyListText(getString(R.string.empty_departure_timetable));
			}
			else {
				fillList(departureTimetable);
				updateList();
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

		getMenuInflater().inflate(R.menu.departure_times_context_menu_items, menu);
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

		Intent callIntent = IntentFactory
			.createDepartureTimeEditingIntent(activityContext, route, time);
		startActivity(callIntent);
	}

	private Time getTime(int timePosition) {
		SimpleAdapter timetableAdapter = (SimpleAdapter) getListAdapter();

		@SuppressWarnings("unchecked")
		Map<String, Object> adapterItem = (Map<String, Object>) timetableAdapter.getItem(timePosition);

		return (Time) adapterItem.get(LIST_ITEM_OBJECT_ID);
	}

	private void callDepartureTimeDeleting(int timePosition) {
		new DeleteDepartureTimeTask(timePosition).execute();
	}

	private class DeleteDepartureTimeTask extends AsyncTask<Void, Void, Void>
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
				setEmptyListText(getString(R.string.empty_departure_timetable));
			}
		}

		@Override
		protected Void doInBackground(Void... params) {
			route.removeDepartureTime(time);

			return null;
		}
	}
}