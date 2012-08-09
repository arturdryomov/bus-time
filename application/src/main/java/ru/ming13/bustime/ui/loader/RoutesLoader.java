package ru.ming13.bustime.ui.loader;


import java.util.List;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import ru.ming13.bustime.db.DbProvider;
import ru.ming13.bustime.db.model.Route;
import ru.ming13.bustime.db.model.Station;


public class RoutesLoader extends AsyncTaskLoader<List<Route>>
{
	private static enum Mode
	{
		ALL, FOR_STATION
	}

	private final Mode mode;

	private Station station;

	public RoutesLoader(Context context) {
		super(context);

		mode = Mode.ALL;
	}

	public RoutesLoader(Context context, Station station) {
		super(context);

		this.station = station;

		mode = Mode.FOR_STATION;
	}

	@Override
	protected void onStartLoading() {
		super.onStartLoading();

		forceLoad();
	}

	@Override
	public List<Route> loadInBackground() {
		switch (mode) {
			case ALL:
				return DbProvider.getInstance().getRoutes().getRoutesList();

			case FOR_STATION:
				return DbProvider.getInstance().getRoutes().getRoutesList(station);

			default:
				throw new LoaderException();
		}
	}
}
