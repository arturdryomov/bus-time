package ru.ming13.bustime.ui.fragment;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import ru.ming13.bustime.R;
import ru.ming13.bustime.db.model.Route;
import ru.ming13.bustime.db.model.Station;
import ru.ming13.bustime.db.time.Time;
import ru.ming13.bustime.ui.intent.IntentFactory;
import ru.ming13.bustime.ui.loader.Loaders;
import ru.ming13.bustime.ui.loader.StationsForRouteLoader;
import ru.ming13.bustime.ui.util.EveryMinuteActionPerformer;


public class StationsForRouteFragment extends AdaptedListFragment<Map<Station, Time>> implements LoaderManager.LoaderCallbacks<List<Map<Station, Time>>>, EveryMinuteActionPerformer.EveryMinuteCallback
{
	private static enum Order
	{
		BY_NAME, BY_TIME_SHIFT
	}

	private Order order = Order.BY_TIME_SHIFT;

	private static final String LIST_ITEM_NAME_ID = "name";
	private static final String LIST_ITEM_REMAINING_TIME_ID = "remaining_time";

	private final EveryMinuteActionPerformer everyMinuteActionPerformer;

	private Route route;

	public StationsForRouteFragment() {
		super();

		everyMinuteActionPerformer = new EveryMinuteActionPerformer(this);
	}

	public static StationsForRouteFragment newInstance(Route route) {
		StationsForRouteFragment stationsForRouteFragment = new StationsForRouteFragment();

		stationsForRouteFragment.setArguments(buildArguments(route));

		return stationsForRouteFragment;
	}

	private static Bundle buildArguments(Route route) {
		Bundle arguments = new Bundle();

		arguments.putParcelable(FragmentArguments.ROUTE, route);

		return arguments;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		route = getArguments().getParcelable(FragmentArguments.ROUTE);
	}

	@Override
	protected SimpleAdapter buildListAdapter() {
		return new SimpleAdapter(getActivity(), list, R.layout.list_item_two_line,
			new String[] {LIST_ITEM_NAME_ID, LIST_ITEM_REMAINING_TIME_ID},
			new int[] {R.id.text_first_line, R.id.text_second_line});
	}

	@Override
	protected Map<String, Object> buildListItem(Map<Station, Time> stationAndTime) {
		Map<String, Object> listItem = new HashMap<String, Object>();

		Station station = getStation(stationAndTime);
		Time time = getTime(stationAndTime);

		listItem.put(LIST_ITEM_OBJECT_ID, stationAndTime);
		listItem.put(LIST_ITEM_NAME_ID, station.getName());
		listItem.put(LIST_ITEM_REMAINING_TIME_ID, buildRemainingTimeText(time));

		return listItem;
	}

	private Station getStation(Map<Station, Time> stationAndTime) {
		return stationAndTime.keySet().iterator().next();
	}

	private Time getTime(Map<Station, Time> stationAndTime) {
		return stationAndTime.get(getStation(stationAndTime));
	}

	private String buildRemainingTimeText(Time time) {
		if (time == null) {
			return getString(R.string.token_no_trips);
		}

		if (time.isNow()) {
			return getString(R.string.token_time_now);
		}

		return time.toRelativeToNowSpanString();
	}

	@Override
	protected void callListPopulation() {
		// It would be populated with list navigation automatic
	}

	@Override
	public void callListRepopulation() {
		clearList();
		setEmptyListText(R.string.loading_stations);

		getLoaderManager().restartLoader(Loaders.STATIONS_FOR_ROUTE, null, this);
	}

	@Override
	public Loader<List<Map<Station, Time>>> onCreateLoader(int loaderId, Bundle loaderArguments) {
		switch (order) {
			case BY_NAME:
				return StationsForRouteLoader.newNameOrderedInstance(getActivity(), route);

			case BY_TIME_SHIFT:
				return StationsForRouteLoader.newTimeShiftOrderedInstance(getActivity(), route);

			default:
				return StationsForRouteLoader.newNameOrderedInstance(getActivity(), route);
		}
	}

	@Override
	public void onLoadFinished(Loader<List<Map<Station, Time>>> stationsForRouteLoader, List<Map<Station, Time>> stationsAndTimes) {
		if (stationsAndTimes.isEmpty()) {
			setEmptyListText(R.string.empty_stations);
		}
		else {
			populateList(stationsAndTimes);
		}
	}

	@Override
	public void onLoaderReset(Loader<List<Map<Station, Time>>> stationsForRouteLoader) {
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);

		Station selectedStation = getStation(getListItemObject(position));
		callTimetableActivity(selectedStation);
	}

	private void callTimetableActivity(Station station) {
		Intent callIntent = IntentFactory.createTimetableIntent(getActivity(), route, station);
		startActivity(callIntent);
	}

	public void sortByName() {
		order = Order.BY_NAME;

		callListRepopulation();
	}

	public void sortByTimeShift() {
		order = Order.BY_TIME_SHIFT;

		callListRepopulation();
	}

	@Override
	public void onEveryMinute() {
		updateRemainingTimes();
	}

	private void updateRemainingTimes() {
		for (int listPosition = 0; listPosition < list.size(); listPosition++) {
			Map<Station, Time> stationAndTime = getListItemObject(listPosition);

			list.get(listPosition).put(LIST_ITEM_REMAINING_TIME_ID,
				buildRemainingTimeText(getTime(stationAndTime)));
		}

		refreshListContent();
	}

	@Override
	public void onResume() {
		super.onResume();

		updateRemainingTimes();

		everyMinuteActionPerformer.startPerforming();
	}

	@Override
	public void onPause() {
		super.onPause();

		everyMinuteActionPerformer.stopPerforming();
	}
}
