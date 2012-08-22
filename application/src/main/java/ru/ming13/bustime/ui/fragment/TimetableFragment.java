package ru.ming13.bustime.ui.fragment;


import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.widget.SimpleAdapter;
import ru.ming13.bustime.R;
import ru.ming13.bustime.db.model.Route;
import ru.ming13.bustime.db.model.Station;
import ru.ming13.bustime.db.time.Time;
import ru.ming13.bustime.ui.loader.Loaders;
import ru.ming13.bustime.ui.loader.TimetableLoader;


public class TimetableFragment extends AdaptedListFragment<Time> implements LoaderManager.LoaderCallbacks<List<Time>>
{
	private static enum Mode
	{
		FULL_WEEK, WORKDAYS, WEEKEND
	}

	private static final String LIST_ITEM_TIME_ID = "time";
	private static final String LIST_ITEM_REMAINING_TIME_ID = "remaining_time";

	private static final int PREVIOUS_TIMES_DISPLAYED_COUNT = 1;

	private final Handler remainingTimeTextUpdateTimer = new Handler();
	private static final int AUTO_UPDATE_MILLISECONDS_PERIOD = 60000;

	private Mode mode;

	private Route route;
	private Station station;

	private Time currentTime;

	public static TimetableFragment newFullWeekInstance(Route route, Station station) {
		TimetableFragment timetableFragment = new TimetableFragment();

		timetableFragment.setArguments(buildArguments(Mode.FULL_WEEK, route, station));

		return timetableFragment;
	}

	private static Bundle buildArguments(Mode mode, Route route, Station station) {
		Bundle arguments = new Bundle();

		arguments.putSerializable(FragmentArguments.MODE, mode);
		arguments.putParcelable(FragmentArguments.ROUTE, route);
		arguments.putParcelable(FragmentArguments.STATION, station);

		return arguments;
	}

	public static TimetableFragment newWorkdaysInstance(Route route, Station station) {
		TimetableFragment timetableFragment = new TimetableFragment();

		timetableFragment.setArguments(buildArguments(Mode.WORKDAYS, route, station));

		return timetableFragment;
	}

	public static TimetableFragment newWeekendInstance(Route route, Station station) {
		TimetableFragment timetableFragment = new TimetableFragment();

		timetableFragment.setArguments(buildArguments(Mode.WEEKEND, route, station));

		return timetableFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mode = (Mode) getArguments().getSerializable(FragmentArguments.MODE);
		route = getArguments().getParcelable(FragmentArguments.ROUTE);
		station = getArguments().getParcelable(FragmentArguments.STATION);
	}

	@Override
	protected SimpleAdapter buildListAdapter() {
		String[] listColumnNames = {LIST_ITEM_TIME_ID, LIST_ITEM_REMAINING_TIME_ID};
		int[] columnCorrespondingResources = {R.id.text_first_line, R.id.text_second_line};

		return new SimpleAdapter(getActivity(), list, R.layout.list_item_two_line, listColumnNames,
			columnCorrespondingResources);
	}

	@Override
	protected Map<String, Object> buildListItem(Time time) {
		Map<String, Object> listItem = new HashMap<String, Object>();

		listItem.put(LIST_ITEM_OBJECT_ID, time);
		listItem.put(LIST_ITEM_TIME_ID, time.toSystemFormattedString(getActivity()));
		listItem.put(LIST_ITEM_REMAINING_TIME_ID, buildRemainingTimeText(time));

		return listItem;
	}

	private String buildRemainingTimeText(Time busTime) {
		if (busTime.isNow()) {
			return getString(R.string.token_time_now);
		}

		return busTime.toRelativeToNowSpanString();
	}

	@Override
	protected void callListPopulation() {
		setEmptyListText(R.string.loading_timetable);

		getLoaderManager().initLoader(Loaders.TIMETABLE, null, this);
	}

	@Override
	public Loader<List<Time>> onCreateLoader(int loaderId, Bundle loaderArguments) {
		switch (mode) {
			case FULL_WEEK:
				return TimetableLoader.newFullWeekLoader(getActivity(), route, station);

			case WORKDAYS:
				return TimetableLoader.newWorkdaysLoader(getActivity(), route, station);

			case WEEKEND:
				return TimetableLoader.newWeekendLoader(getActivity(), route, station);

			default:
				return TimetableLoader.newFullWeekLoader(getActivity(), route, station);
		}
	}

	@Override
	public void onLoadFinished(Loader<List<Time>> timetableLoader, List<Time> timetable) {
		if (timetable.isEmpty()) {
			setEmptyListText(R.string.empty_timetable);
		}
		else {
			populateList(timetable);

			placeClosestTimeOnTop();
		}
	}

	private void placeClosestTimeOnTop() {
		setSelection(getClosestTimeListPosition() - PREVIOUS_TIMES_DISPLAYED_COUNT);
	}

	private int getClosestTimeListPosition() {
		int closestTimeListPosition = 0;

		for (int listPosition = 0; listPosition < list.size(); listPosition++) {
			Time listDataTime = getListItemObject(listPosition);

			if (listDataTime.isAfter(currentTime)) {
				closestTimeListPosition = listPosition;

				break;
			}
		}

		return closestTimeListPosition;
	}

	@Override
	public void onLoaderReset(Loader<List<Time>> listLoader) {
	}

	@Override
	public void callListRepopulation() {
		setEmptyListText(R.string.loading_timetable);

		getLoaderManager().restartLoader(Loaders.TIMETABLE, null, this);
	}

	@Override
	public void onResume() {
		super.onResume();

		updateRemainingTimes();
		startUpdatingRemainingTimeText();
	}

	private void updateRemainingTimes() {
		currentTime = Time.newInstance();

		for (Map<String, Object> listDataElement : list) {
			Time listDataTime = (Time) listDataElement.get(LIST_ITEM_OBJECT_ID);

			listDataElement.put(LIST_ITEM_REMAINING_TIME_ID, buildRemainingTimeText(listDataTime));
		}

		refreshListContent();
	}

	private void startUpdatingRemainingTimeText() {
		stopUpdatingRemainingTimeText();

		remainingTimeTextUpdateTimer.postDelayed(remainingTimeTextUpdateTask,
			calculateMillisecondsForNextMinute());
	}

	private void stopUpdatingRemainingTimeText() {
		remainingTimeTextUpdateTimer.removeCallbacks(remainingTimeTextUpdateTask);
	}

	private long calculateMillisecondsForNextMinute() {
		Calendar currentTime = Calendar.getInstance();

		Calendar nextMinuteTime = Calendar.getInstance();
		nextMinuteTime.add(Calendar.MINUTE, 1);
		nextMinuteTime.set(Calendar.SECOND, 0);

		return nextMinuteTime.getTimeInMillis() - currentTime.getTimeInMillis();
	}

	private final Runnable remainingTimeTextUpdateTask = new Runnable()
	{
		@Override
		public void run() {
			updateRemainingTimes();

			continueUpdatingRemainingTimeText();
		}
	};

	private void continueUpdatingRemainingTimeText() {
		remainingTimeTextUpdateTimer.postDelayed(remainingTimeTextUpdateTask,
			AUTO_UPDATE_MILLISECONDS_PERIOD);
	}

	@Override
	public void onPause() {
		super.onPause();

		stopUpdatingRemainingTimeText();
	}
}
