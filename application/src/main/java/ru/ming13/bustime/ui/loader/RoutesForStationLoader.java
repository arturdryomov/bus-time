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
import ru.ming13.bustime.db.time.Time;
import ru.ming13.bustime.db.time.TimeException;


public class RoutesForStationLoader extends AsyncTaskLoader<List<Map<Route, Time>>>
{
	private final Station station;

	public RoutesForStationLoader(Context context, Station station) {
		super(context);

		this.station = station;
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

		return result;
	}

	private Map<Route, Time> buildResultItem(Route route) {
		Map<Route, Time> resultItem = new HashMap<Route, Time>();

		try {
			resultItem.put(route, getClosestTrip(route));
		}
		catch (TimeException e) {
			resultItem.put(route, null);
		}

		return resultItem;
	}

	private Time getClosestTrip(Route route) {
		if (!route.isWeekPartDependent()) {
			return station.getClosestFullWeekTrip(route);
		}

		if (Time.newInstance().isWeekend()) {
			return station.getClosestWeekendTrip(route);
		}
		else {
			return station.getClosestWorkdaysTrip(route);
		}
	}
}
