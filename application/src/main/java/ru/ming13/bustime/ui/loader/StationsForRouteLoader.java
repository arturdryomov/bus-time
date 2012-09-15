package ru.ming13.bustime.ui.loader;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import ru.ming13.bustime.db.DbProvider;
import ru.ming13.bustime.db.model.Route;
import ru.ming13.bustime.db.model.Station;
import ru.ming13.bustime.db.sqlite.DbFieldValues;
import ru.ming13.bustime.db.time.Time;
import ru.ming13.bustime.db.time.TimeException;


public class StationsForRouteLoader extends AsyncTaskLoader<List<Map<Station, Time>>>
{
	private static enum Order
	{
		BY_NAME, BY_TIME_SHIFT
	}

	private final Order order;

	private final Route route;
	private int routeTripsType;

	public static StationsForRouteLoader newNameOrderedInstance(Context context, Route route) {
		return new StationsForRouteLoader(context, route, Order.BY_NAME);
	}

	private StationsForRouteLoader(Context context, Route route, Order order) {
		super(context);

		this.route = route;

		this.order = order;
	}

	public static StationsForRouteLoader newTimeShiftOrderedInstance(Context context, Route route) {
		return new StationsForRouteLoader(context, route, Order.BY_TIME_SHIFT);
	}

	@Override
	protected void onStartLoading() {
		super.onStartLoading();

		forceLoad();
	}

	@Override
	public List<Map<Station, Time>> loadInBackground() {
		List<Map<Station, Time>> result = new ArrayList<Map<Station, Time>>();

		routeTripsType = getRouteTripsType();

		for (Station station : getStations()) {
			result.add(buildResultItem(station));
		}

		return result;
	}

	private int getRouteTripsType() {
		if (!route.isWeekPartDependent()) {
			return DbFieldValues.TRIP_FULL_WEEK_ID;
		}

		if (Time.newInstance().isWeekend()) {
			return DbFieldValues.TRIP_WEEKEND_ID;
		}
		else {
			return DbFieldValues.TRIP_WORKDAY_ID;
		}
	}

	private List<Station> getStations() {
		switch (order) {
			case BY_NAME:
				return DbProvider.getInstance().getStations().getStationsListOrderedByName(route);

			case BY_TIME_SHIFT:
				return DbProvider.getInstance().getStations().getStationsListOrderedByTimeShift(route);

			default:
				return DbProvider.getInstance().getStations().getStationsListOrderedByName(route);
		}
	}

	private Map<Station, Time> buildResultItem(Station station) {
		Map<Station, Time> resultItem = new HashMap<Station, Time>();

		try {
			resultItem.put(station, getClosestBusTime(station));
		}
		catch (TimeException e) {
			resultItem.put(station, null);
		}

		return resultItem;
	}

	private Time getClosestBusTime(Station station) {
		switch (routeTripsType) {
			case DbFieldValues.TRIP_FULL_WEEK_ID:
				return station.getClosestFullWeekBusTime(route);

			case DbFieldValues.TRIP_WEEKEND_ID:
				return station.getClosestWeekendBusTime(route);

			case DbFieldValues.TRIP_WORKDAY_ID:
				return station.getClosestWorkdaysBusTime(route);

			default:
				return station.getClosestFullWeekBusTime(route);
		}
	}
}
