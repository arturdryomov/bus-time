package app.android.bustime.ui;


import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import app.android.bustime.R;
import app.android.bustime.db.Route;
import app.android.bustime.db.Station;
import app.android.bustime.db.Time;


public class TimetableActivity extends SimpleAdapterListActivity
{
	private final Context activityContext = this;

	private Route route;
	private Station station;

	private Time currentTime;

	private static final String LIST_ITEM_TIME_ID = "time";
	private static final String LIST_ITEM_REMAINING_TIME_ID = "remaining_time";
	private static final String LIST_ITEM_OBJECT_ID = "object";

	private final Handler timer;
	private static final int AUTO_UPDATE_MILLISECONDS_PERIOD = 60000;

	private static final int CENTER_TIME_TOP_PADDING_PROPORTION = 3;

	public TimetableActivity() {
		super();

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
				fillList(timetable);

				placeClosestTimeOnCenter();
			}
		}
	}

	@Override
	protected void addItemToList(Object itemData) {
		Time time = (Time) itemData;

		HashMap<String, Object> timeItem = new HashMap<String, Object>();

		timeItem.put(LIST_ITEM_TIME_ID, time.toString(activityContext));
		timeItem.put(LIST_ITEM_REMAINING_TIME_ID, constructRemainingTimeText(time));
		timeItem.put(LIST_ITEM_OBJECT_ID, time);

		listData.add(timeItem);
	}

	private String constructRemainingTimeText(Time busTime) {
		currentTime = Time.getCurrentTime();

		if (busTime.equals(currentTime)) {
			return getString(R.string.token_time_now);
		}

		return DateUtils.getRelativeTimeSpanString(busTime.getMilliseconds(),
			currentTime.getMilliseconds(), DateUtils.MINUTE_IN_MILLIS).toString();
	}

	private void placeClosestTimeOnCenter() {
		int timePosition = getClosestTimePosition();
		int topPadding = getListViewHeight() / CENTER_TIME_TOP_PADDING_PROPORTION;

		getListView().setSelectionFromTop(timePosition, topPadding);
	}

	private int getClosestTimePosition() {
		int closestTimePosition = 0;

		for (int adapterPosition = 0; adapterPosition < listData.size(); adapterPosition++) {
			Time listDataTime = (Time) listData.get(adapterPosition).get(LIST_ITEM_OBJECT_ID);

			if (listDataTime.isAfter(currentTime)) {
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
		SimpleAdapter timetableAdapter = new SimpleAdapter(activityContext, listData,
			R.layout.list_item_two_line, new String[] { LIST_ITEM_TIME_ID, LIST_ITEM_REMAINING_TIME_ID },
			new int[] { R.id.text_first_line, R.id.text_second_line });

		setListAdapter(timetableAdapter);

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
			Time listDataTime = (Time) listDataElement.get(LIST_ITEM_OBJECT_ID);

			listDataElement.put(LIST_ITEM_REMAINING_TIME_ID,
				constructRemainingTimeText(listDataTime));
		}

		updateList();
	}

	private void startUpdatingRemainingTimeText() {
		stopUpdatingRemainingTimeText();

		timer.postDelayed(timerTask, calculateMillisecondsForNextMinute());
	}

	private long calculateMillisecondsForNextMinute() {
		Calendar currentTime = Calendar.getInstance();

		Calendar nextMinuteTime = Calendar.getInstance();
		nextMinuteTime.add(Calendar.MINUTE, 1);
		nextMinuteTime.set(Calendar.SECOND, 0);

		return nextMinuteTime.getTimeInMillis() - currentTime.getTimeInMillis();
	}

	private void stopUpdatingRemainingTimeText() {
		timer.removeCallbacks(timerTask);
	}

	private final Runnable timerTask = new Runnable() {
		@Override
		public void run() {
			updateRemainingTimes();

			continueUpdatingRemainingTimeText();
		}
	};

	private void continueUpdatingRemainingTimeText() {
		timer.postDelayed(timerTask, AUTO_UPDATE_MILLISECONDS_PERIOD);
	}

	@Override
	protected void onPause() {
		super.onPause();

		stopUpdatingRemainingTimeText();
	}
}
