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
import ru.ming13.bustime.ui.loader.RoutesForStationLoader;
import ru.ming13.bustime.ui.util.EveryMinuteActionPerformer;


public class RoutesForStationFragment extends AdaptedListFragment<Map<Route, Time>> implements LoaderManager.LoaderCallbacks<List<Map<Route, Time>>>, EveryMinuteActionPerformer.EveryMinuteCallback
{
	private static enum Order
	{
		BY_NAME, BY_BUS_TIME
	}

	private Order order;

	private static final String LIST_ITEM_NAME_ID = "name";
	private static final String LIST_ITEM_REMAINING_TIME_ID = "remaining_time";

	private final EveryMinuteActionPerformer everyMinuteActionPerformer;

	private Station station;

	public RoutesForStationFragment() {
		super();

		order = Order.BY_BUS_TIME;

		everyMinuteActionPerformer = new EveryMinuteActionPerformer(this);
	}

	public static RoutesForStationFragment newInstance(Station station) {
		RoutesForStationFragment routesForStationFragment = new RoutesForStationFragment();

		routesForStationFragment.setArguments(buildArguments(station));

		return routesForStationFragment;
	}

	private static Bundle buildArguments(Station station) {
		Bundle arguments = new Bundle();

		arguments.putParcelable(FragmentArguments.STATION, station);

		return arguments;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		station = getArguments().getParcelable(FragmentArguments.STATION);
	}

	@Override
	protected SimpleAdapter buildListAdapter() {
		return new SimpleAdapter(getActivity(), list, R.layout.list_item_two_line,
			new String[] {LIST_ITEM_NAME_ID, LIST_ITEM_REMAINING_TIME_ID},
			new int[] {R.id.text_first_line, R.id.text_second_line});
	}

	@Override
	protected Map<String, Object> buildListItem(Map<Route, Time> routeAndTime) {
		Map<String, Object> listItem = new HashMap<String, Object>();

		Route route = getRoute(routeAndTime);
		Time time = getTime(routeAndTime);

		listItem.put(LIST_ITEM_OBJECT_ID, routeAndTime);
		listItem.put(LIST_ITEM_NAME_ID, route.getName());
		listItem.put(LIST_ITEM_REMAINING_TIME_ID, buildRemainingTimeText(time));

		return listItem;
	}

	private Route getRoute(Map<Route, Time> routeAndTime) {
		return routeAndTime.keySet().iterator().next();
	}

	private Time getTime(Map<Route, Time> routeAndTime) {
		return routeAndTime.get(getRoute(routeAndTime));
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
		setEmptyListText(R.string.loading_routes);

		getLoaderManager().restartLoader(Loaders.ROUTES_FOR_STATION, null, this);
	}

	@Override
	public Loader<List<Map<Route, Time>>> onCreateLoader(int loaderId, Bundle loaderArguments) {
		switch (order) {
			case BY_NAME:
				return RoutesForStationLoader.newNameOrderedInstance(getActivity(), station);

			case BY_BUS_TIME:
				return RoutesForStationLoader.newBusTimeOrderedInstance(getActivity(), station);

			default:
				return RoutesForStationLoader.newBusTimeOrderedInstance(getActivity(), station);
		}
	}

	@Override
	public void onLoadFinished(Loader<List<Map<Route, Time>>> routesForStationLoader, List<Map<Route, Time>> routesAndTimes) {
		if (routesAndTimes.isEmpty()) {
			setEmptyListText(R.string.empty_routes);
		}
		else {
			populateList(routesAndTimes);
		}
	}

	@Override
	public void onLoaderReset(Loader<List<Map<Route, Time>>> routesForStationLoader) {
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);

		Route selectedRoute = getRoute(getListItemObject(position));
		callTimetableActivity(selectedRoute);
	}

	private void callTimetableActivity(Route route) {
		Intent callIntent = IntentFactory.createTimetableIntent(getActivity(), route, station);
		startActivity(callIntent);
	}

	public void sortByName() {
		order = Order.BY_NAME;

		callListRepopulation();
	}

	public void sortByBusTime() {
		order = Order.BY_BUS_TIME;

		callListRepopulation();
	}

	@Override
	public void onEveryMinute() {
		updateRemainingTimes();
	}

	private void updateRemainingTimes() {
		for (int listPosition = 0; listPosition < list.size(); listPosition++) {
			Map<Route, Time> routeAndTime = getListItemObject(listPosition);

			list.get(listPosition).put(LIST_ITEM_REMAINING_TIME_ID,
				buildRemainingTimeText(getTime(routeAndTime)));
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