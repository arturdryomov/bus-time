package ru.ming13.bustime.ui.loader;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import org.apache.commons.lang3.tuple.Pair;
import ru.ming13.bustime.db.DbProvider;
import ru.ming13.bustime.db.model.Route;
import ru.ming13.bustime.db.model.Station;
import ru.ming13.bustime.db.time.Time;
import ru.ming13.bustime.db.time.TimeException;


public class RoutesForStationLoader extends AsyncTaskLoader<List<Pair<Route, Time>>>
{
	private static enum Order
	{
		BY_NAME, BY_BUS_TIME
	}

	private final Order order;

	private final Station station;

	private final boolean isWeekend;

	public static RoutesForStationLoader newNameOrderedInstance(Context context, Station station) {
		return new RoutesForStationLoader(context, station, Order.BY_NAME);
	}

	private RoutesForStationLoader(Context context, Station station, Order order) {
		super(context);

		this.station = station;

		this.order = order;

		this.isWeekend = Time.newInstance().isWeekend();
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
	public List<Pair<Route, Time>> loadInBackground() {
		List<Pair<Route, Time>> result = new ArrayList<Pair<Route, Time>>();

		for (Route route : DbProvider.getInstance().getRoutes().getRoutesList(station)) {
			result.add(buildResultItem(route));
		}

		reorderRoutesAndTimes(result);

		return result;
	}

	private Pair<Route, Time> buildResultItem(Route route) {
		Time closestBusTime;

		try {
			closestBusTime = getClosestBusTime(route);
		}
		catch (TimeException e) {
			closestBusTime = null;
		}

		return Pair.of(route, closestBusTime);
	}

	private Time getClosestBusTime(Route route) {
		if (!route.isWeekPartDependent()) {
			return station.getClosestFullWeekBusTime(route);
		}

		if (isWeekend) {
			return station.getClosestWeekendBusTime(route);
		}
		else {
			return station.getClosestWorkdaysBusTime(route);
		}
	}

	private void reorderRoutesAndTimes(List<Pair<Route, Time>> routesAndTimes) {
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

	private static final class RoutesByTimeComparator implements Comparator<Pair<Route, Time>>, Serializable
	{
		@Override
		public int compare(Pair<Route, Time> firstRouteAndTime, Pair<Route, Time> secondRouteAndTime) {
			Time firstRouteTime = firstRouteAndTime.getRight();
			Time secondRouteTime = secondRouteAndTime.getRight();

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
	}
}
