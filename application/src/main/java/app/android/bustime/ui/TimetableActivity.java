package app.android.bustime.ui;


import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import app.android.bustime.R;
import app.android.bustime.local.DbException;
import app.android.bustime.local.Route;
import app.android.bustime.local.Station;
import app.android.bustime.local.Time;


public class TimetableActivity extends SimpleAdapterListActivity
{
	private final Context activityContext = this;

	private Route route;
	private Station station;

	private static final String LIST_ITEM_TEXT_ID = "text";
	private static final String LIST_ITEM_OBJECT_ID = "object";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timetable);

		processReceivedRouteAndStation();

		initializeList();
	}

	private void processReceivedRouteAndStation() {
		Bundle receivedData = this.getIntent().getExtras();

		if (receivedData.containsKey(IntentFactory.MESSAGE_ID)) {
			if (receivedData.containsKey(IntentFactory.EXTRA_MESSAGE_ID)) {
				route = receivedData.getParcelable(IntentFactory.MESSAGE_ID);
				station = receivedData.getParcelable(IntentFactory.EXTRA_MESSAGE_ID);

				return;
			}
		}

		UserAlerter.alert(activityContext, getString(R.string.someError));

		finish();
	}

	@Override
	protected void initializeList() {
		SimpleAdapter timetetableAdapter = new SimpleAdapter(activityContext, listData,
			R.layout.timetable_list_item, new String[] { LIST_ITEM_TEXT_ID }, new int[] { R.id.text });

		setListAdapter(timetetableAdapter);

		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}

	@Override
	protected void onResume() {
		super.onResume();

		loadTimetable();
	}

	private void loadTimetable() {
		new LoadTimetableTask().execute();
	}

	private class LoadTimetableTask extends AsyncTask<Void, Void, String>
	{
		private List<Time> timetable;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			setEmptyListText(getString(R.string.loadingTimetable));
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				timetable = station.getTimetableForRoute(route);
			}
			catch (DbException e) {
				return getString(R.string.someError);
			}

			return new String();
		}

		@Override
		protected void onPostExecute(String errorMessage) {
			super.onPostExecute(errorMessage);

			if (timetable.isEmpty()) {
				setEmptyListText(getString(R.string.emptyTimetable));
			}
			else {
				fillList(timetable);
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
}
