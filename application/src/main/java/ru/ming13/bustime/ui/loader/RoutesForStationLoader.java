package ru.ming13.bustime.ui.loader;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import ru.ming13.bustime.db.DbProvider;
import ru.ming13.bustime.db.model.Route;
import ru.ming13.bustime.db.model.Station;
import ru.ming13.bustime.db.time.Time;
import ru.ming13.bustime.db.time.TimeException;


public class RoutesForStationLoader extends AsyncTaskLoader<List<Map<Route, Time>>>
{
	private static enum Order
	{
		BY_NAME, BY_BUS_TIME
	}

	private final Order order;

	private final Station station;

	public static RoutesForStationLoader newNameOrderedInstance(Context context, Station station) {
		return new RoutesForStationLoader(context, station, Order.BY_NAME);
	}

	private RoutesForStationLoader(Context context, Station station, Order order) {
		super(context);

		this.station = station;

		this.order = order;
	}

	public static RoutesForStationLoader newBusTimeOrderedInstance(Context context, Station station) {
		return new RoutesForStationLoader(context, station, Order.BY_BUS_TIME);
	}

	@Override
	protected void onStartLoading() {
		super.onStartLoading();

		forceLoad();
	}

	@Override
	public List<Map<Route, Time>> loadInBackground() {
		List<Map<Route, Time>> result = new ArrayList<Map<Route, Time>>();

		for (Route route : DbProvider.getInstance().getRoutes().getRoutesList(station)) {
			result.add(buildResultItem(route));
		}

		reorderRoutesAndTimes(result);

		return result;
	}

	private Map<Route, Time> buildResultItem(Route route) {
		Map<Route, Time> resultItem = new HashMap<Route, Time>();

		try {
			resultItem.put(route, getClosestBusTime(route));
		}
		catch (TimeException e) {
			resultItem.put(route, null);
		}

		return resultItem;
	}

	private Time getClosestBusTime(Route route) {
		if (!route.isWeekPartDependent()) {
			return station.getClosestFullWeekBusTime(route);
		}

		if (Time.newInstance().isWeekend()) {
			return station.getClosestWeekendBusTime(route);
		}
		else {
			return station.getClosestWorkdaysBusTime(route);
		}
	}

	private void reorderRoutesAndTimes(List<Map<Route, Time>> routesAndTimes) {
		switch (order) {
			case BY_NAME:
				// It’s already sorted by name by database
				break;

			case BY_BUS_TIME:
				Collections.sort(routesAndTimes, new RoutesByTimeComparator());
				break;

			default:
				break;
		}
	}

	private static final class RoutesByTimeComparator implements Comparator<Map<Route, Time>>, Serializable
	{
		@Override
		public int compare(Map<Route, Time> firstRouteAndTime, Map<Route, Time> secondRouteAndTime) {
			Time firstRouteTime = getTime(firstRouteAndTime);
			Time secondRouteTime = getTime(secondRouteAndTime);

			if ((firstRouteTime == null) && (secondRouteAndTime == null)) {
				return 0;
			}

			if (firstRouteTime == null) {
				return 1;
			}

			if (secondRouteTime == null) {
				return -1;
			}

			if (firstRouteTime.toDatabaseString().equals(secondRouteTime.toDatabaseString())) {
				return 0;
			}

			if (firstRouteTime.isAfter(secondRouteTime)) {
				return 1;
			}
			else {
				return -1;
			}
		}

		private Time getTime(Map<Route, Time> routeAndTime) {
			return routeAndTime.get(getRoute(routeAndTime));
		}

		private Route getRoute(Map<Route, Time> routeAndTime) {
			return routeAndTime.keySet().iterator().next();
		}
	}
}