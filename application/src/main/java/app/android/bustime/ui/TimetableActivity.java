package app.android.bustime.ui;


import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
	private final HumanTimeFormatter timeFormatter;

	private static final String LIST_ITEM_TIME_ID = "time";
	private static final String LIST_ITEM_REMAINING_TIME_ID = "remaining_time";
	private static final String LIST_ITEM_OBJECT_ID = "object";

	private final Handler timer;
	private static final int AUTO_UPDATE_SECONDS_PERIOD = 60;

	public TimetableActivity() {
		super();

		timeFormatter = new HumanTimeFormatter(activityContext);

		timer = new Handler();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timetable);

		processReceivedRouteAndStation();
		loadTimetable();

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

		UserAlerter.alert(activityContext, getString(R.string.error_unspecified));

		finish();
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

			setEmptyListText(getString(R.string.loading_timetable));
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
				setEmptyListText(getString(R.string.empty_timetable));
			}
			else {
				currentTime = Time.getCurrentTime();

				fillList(timetable);

				placeClosestTimeOnCenter();
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
		if (busTime.equals(currentTime)) {
			return getString(R.string.token_time_now);
		}

		if (busTime.isAfter(currentTime)) {
			Time timeDifference = busTime.difference(currentTime);

			return String.format("%s %s", getString(R.string.token_time_in),
				timeFormatter.toHumanFormat(timeDifference));
		}
		else {
			Time timeDifference = currentTime.difference(busTime);

			return String.format("%s %s", timeFormatter.toHumanFormat(timeDifference),
				getString(R.string.token_time_ago));
		}
	}

	private void placeClosestTimeOnCenter() {
		int timePosition = getClosestTimePosition();
		int topPadding = getListViewHeight() / 3;

		getListView().setSelectionFromTop(timePosition, topPadding);
	}

	private int getClosestTimePosition() {
		int closestTimePosition = 0;

		for (int adapterPosition = 0; adapterPosition < listData.size(); adapterPosition++) {
			Time listDataElementTime = (Time) listData.get(adapterPosition).get(LIST_ITEM_OBJECT_ID);

			if (listDataElementTime.isAfter(currentTime)) {
				closestTimePosition = adapterPosition;

				break;
			}
		}

		return closestTimePosition;
	}

	private int getListViewHeight() {
		int displayHeight = getWindowManager().getDefaultDisplay().getHeight();
		int actionbarHeight = (int) getResources().getDimension(R.dimen.actionbar_height);

		return displayHeight - actionbarHeight;
	}

	@Override
	protected void initializeList() {
		SimpleAdapter timetetableAdapter = new SimpleAdapter(activityContext, listData,
			R.layout.list_item_two_line, new String[] { LIST_ITEM_TIME_ID, LIST_ITEM_REMAINING_TIME_ID },
			new int[] { R.id.first_line, R.id.second_line });

		setListAdapter(timetetableAdapter);

		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}

	@Override
	protected void onResume() {
		super.onResume();

		updateRemainingTimes();
		startUpdatingRemainingTimeText();
	}

	private void updateRemainingTimes() {
		currentTime = Time.getCurrentTime();

		for (HashMap<String, Object> listDataElement : listData) {
			Time listDataElementTime = (Time) listDataElement.get(LIST_ITEM_OBJECT_ID);

			listDataElement.put(LIST_ITEM_REMAINING_TIME_ID,
				constructRemainingTimeText(listDataElementTime));
		}

		updateList();
	}

	private void startUpdatingRemainingTimeText() {
		stopUpdatingRemainingTimeText();

		timer.postDelayed(timerTask, convertSecondsToMilliseconds(AUTO_UPDATE_SECONDS_PERIOD));
	}

	private void stopUpdatingRemainingTimeText() {
		timer.removeCallbacks(timerTask);
	}

	private final Runnable timerTask = new Runnable() {
		@Override
		public void run() {
			updateRemainingTimes();

			startUpdatingRemainingTimeText();
		}
	};

	private long convertSecondsToMilliseconds(int secondsCount) {
		final int millisecondsInSecondCount = 1000;

		return millisecondsInSecondCount * secondsCount;
	}

	@Override
	protected void onPause() {
		super.onPause();

		stopUpdatingRemainingTimeText();
	}
}
