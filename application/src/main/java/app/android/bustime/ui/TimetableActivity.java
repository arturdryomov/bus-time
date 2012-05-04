package app.android.bustime.ui;


import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import app.android.bustime.R;
import app.android.bustime.local.Route;
import app.android.bustime.local.Station;
import app.android.bustime.local.Time;


public class TimetableActivity extends SimpleAdapterListActivity
{
	private final Context activityContext = this;

	private Route route;
	private Station station;

	private Time currentTime;

	private static final String LIST_ITEM_TIME_ID = "time";
	private static final String LIST_ITEM_REMAINING_TIME_ID = "remaining_time";
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
			R.layout.two_line_list_item, new String[] { LIST_ITEM_TIME_ID, LIST_ITEM_REMAINING_TIME_ID },
			new int[] { R.id.first_line, R.id.second_line });

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

	private class LoadTimetableTask extends AsyncTask<Void, Void, Void>
	{
		private List<Time> timetable;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			setEmptyListText(getString(R.string.loadingTimetable));
		}

		@Override
		protected Void doInBackground(Void... params) {
			timetable = station.getTimetableForRoute(route);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (timetable.isEmpty()) {
				setEmptyListText(getString(R.string.emptyTimetable));
			}
			else {
				currentTime = Time.getCurrentTime();

				fillList(timetable);
				updateList();
			}
		}
	}

	@Override
	protected void addItemToList(Object itemData) {
		Time time = (Time) itemData;

		HashMap<String, Object> timeItem = new HashMap<String, Object>();

		timeItem.put(LIST_ITEM_TIME_ID, time.toString());
		timeItem.put(LIST_ITEM_REMAINING_TIME_ID, constructRemainingTimeText(time));
		timeItem.put(LIST_ITEM_OBJECT_ID, time);

		listData.add(timeItem);
	}

	private String constructRemainingTimeText(Time busTime) {
		if (busTime.isAfter(currentTime)) {
			Time timeDifference = busTime.difference(currentTime);

			return String.format("%s %s", timeDifference.toString(), getString(R.string.to));
		}
		else {
			Time timeDifference = currentTime.difference(busTime);

			return String.format("%s %s", timeDifference.toString(), getString(R.string.before));
		}
	}
}
