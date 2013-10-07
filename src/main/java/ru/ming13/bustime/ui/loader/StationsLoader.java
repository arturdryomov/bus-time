package ru.ming13.bustime.ui.loader;


import java.util.List;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import ru.ming13.bustime.db.DbProvider;
import ru.ming13.bustime.db.model.Route;
import ru.ming13.bustime.db.model.Station;


public class StationsLoader extends AsyncTaskLoader<List<Station>>
{
	private static enum LoadingMode
	{
		ALL, FOR_ROUTE_SORTED_BY_NAME, FOR_ROUTE_SORTED_BY_TIME_SHIFT, SEARCH
	}

	private final LoadingMode loadingMode;

	private final Route route;

	private final String searchQuery;

	public static StationsLoader newAllLoadingInstance(Context context) {
		return new StationsLoader(context, LoadingMode.ALL, null, null);
	}

	private StationsLoader(Context context, LoadingMode loadingMode, Route route, String searchQuery) {
		super(context);

		this.loadingMode = loadingMode;

		this.route = route;

		this.searchQuery = searchQuery;
	}

	public static StationsLoader newForRouteSortedByNameLoadingInstance(Context context, Route route) {
		return new StationsLoader(context, LoadingMode.FOR_ROUTE_SORTED_BY_NAME, route, null);
	}

	public static StationsLoader newForRouteSortedByTimeShiftLoadingInstance(Context context, Route route) {
		return new StationsLoader(context, LoadingMode.FOR_ROUTE_SORTED_BY_TIME_SHIFT, route, null);
	}

	public static StationsLoader newSearchLoadingInstance(Context context, String searchQuery) {
		return new StationsLoader(context, LoadingMode.SEARCH, null, searchQuery);
	}

	@Override
	protected void onStartLoading() {
		super.onStartLoading();

		forceLoad();
	}

	@Override
	public List<Station> loadInBackground() {
		switch (loadingMode) {
			case ALL:
				return DbProvider.getInstance().getStations().getStationsList();

			case FOR_ROUTE_SORTED_BY_NAME:
				return DbProvider.getInstance().getStations().getStationsListOrderedByName(route);

			case FOR_ROUTE_SORTED_BY_TIME_SHIFT:
				return DbProvider.getInstance().getStations().getStationsListOrderedByTimeShift(route);

			case SEARCH:
				return DbProvider.getInstance().getStations().getStationsList(searchQuery);

			default:
				throw new LoaderException();
		}
	}
}
