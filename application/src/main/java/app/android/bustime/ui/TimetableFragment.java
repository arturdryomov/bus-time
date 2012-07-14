package app.android.bustime.ui;


import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.format.DateUtils;
import android.widget.SimpleAdapter;
import app.android.bustime.R;
import app.android.bustime.db.Route;
import app.android.bustime.db.Station;
import app.android.bustime.db.Time;


public class TimetableFragment extends AdaptedListFragment
{
	private static final String LIST_ITEM_TIME_ID = "time";
	private static final String LIST_ITEM_REMAINING_TIME_ID = "remaining_time";

	private static final int CENTER_TIME_TOP_PADDING_PROPORTION = 3;

	private final Handler timer;
	private static final int AUTO_UPDATE_MILLISECONDS_PERIOD = 60000;

	private Route route;
	private Station station;

	private Time currentTime;

	public TimetableFragment() {
		super();

		timer = new Handler();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		route = extractRoute();
		station = extractStation();
	}

	private Route extractRoute() {
		if (!FragmentProcessor.haveMessage(getArguments())) {
			UserAlerter.alert(getActivity(), getString(R.string.error_unspecified));
			getActivity().finish();
		}

		return (Route) FragmentProcessor.extractMessage(getArguments());
	}

	private Station extractStation() {
		if (!FragmentProcessor.haveExtraMessage(getArguments())) {
			UserAlerter.alert(getActivity(), getString(R.string.error_unspecified));
			getActivity().finish();
		}

		return (Station) FragmentProcessor.extractExtraMessage(getArguments());
	}

	@Override
	protected SimpleAdapter buildListAdapter() {
		return new SimpleAdapter(getActivity(), list, R.layout.list_item_two_line,
			new String[] {LIST_ITEM_TIME_ID, LIST_ITEM_REMAINING_TIME_ID},
			new int[] {R.id.text_first_line, R.id.test_second_line});
	}

	@Override
	protected Map<String, Object> buildListItem(Object itemObject) {
		Time time = (Time) itemObject;

		Map<String, Object> listItem = new HashMap<String, Object>();

		listItem.put(LIST_ITEM_OBJECT_ID, time);
		listItem.put(LIST_ITEM_TIME_ID, time.toString(getActivity()));
		listItem.put(LIST_ITEM_REMAINING_TIME_ID, constructRemainingTimeText(time));

		return listItem;
	}

	private String constructRemainingTimeText(Time busTime) {
		currentTime = Time.getCurrentTime();

		if (busTime.equals(currentTime)) {
			return getString(R.string.token_time_now);
		}

		return DateUtils.getRelativeTimeSpanString(busTime.getMilliseconds(),
			currentTime.getMilliseconds(), DateUtils.MINUTE_IN_MILLIS).toString();
	}

	@Override
	protected void callListPopulation() {
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
		protected Void doInBackground(Void... parameters) {
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
				populateList(timetable);

				placeClosestTimeOnCenter();
			}
		}
	}

	private void placeClosestTimeOnCenter() {
		int timePosition = getClosestTimePosition();
		int topPadding = getListViewHeight() / CENTER_TIME_TOP_PADDING_PROPORTION;

		getListView().setSelectionFromTop(timePosition, topPadding);
	}

	private int getClosestTimePosition() {
		int closestTimePosition = 0;

		for (int adapterPosition = 0; adapterPosition < list.size(); adapterPosition++) {
			Time listDataTime = (Time) getListItemObject(adapterPosition);

			if (listDataTime.isAfter(currentTime)) {
				closestTimePosition = adapterPosition;

				break;
			}
		}

		return closestTimePosition;
	}

	private int getListViewHeight() {
		int displayHeight = getActivity().getWindowManager().getDefaultDisplay().getHeight();
		int actionbarHeight = (int) getResources().getDimension(R.dimen.height_actionbar);

		return displayHeight - actionbarHeight;
	}

	@Override
	public void onResume() {
		super.onResume();

		updateRemainingTimes();
		startUpdatingRemainingTimeText();
	}

	private void updateRemainingTimes() {
		currentTime = Time.getCurrentTime();

		for (Map<String, Object> listDataElement : list) {
			Time listDataTime = (Time) listDataElement.get(LIST_ITEM_OBJECT_ID);

			listDataElement.put(LIST_ITEM_REMAINING_TIME_ID, constructRemainingTimeText(listDataTime));
		}

		refreshListContent();
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

	private final Runnable timerTask = new Runnable()
	{
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
	public void onPause() {
		super.onPause();

		stopUpdatingRemainingTimeText();
	}
}
