package ru.ming13.bustime.ui.loader;


import java.util.List;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import ru.ming13.bustime.db.DbProvider;
import ru.ming13.bustime.db.model.Route;
import ru.ming13.bustime.db.model.Station;


public class StationsLoader extends AsyncTaskLoader<List<Station>>
{
	private static enum Mode
	{
		ALL, FOR_ROUTE
	}

	private final Mode mode;

	private Route route;

	public StationsLoader(Context context) {
		super(context);

		mode = Mode.ALL;
	}

	public StationsLoader(Context context, Route route) {
		super(context);

		this.route = route;

		mode = Mode.FOR_ROUTE;
	}

	@Override
	protected void onStartLoading() {
		super.onStartLoading();

		forceLoad();
	}

	@Override
	public List<Station> loadInBackground() {
		switch (mode) {
			case ALL:
				return DbProvider.getInstance().getStations().getStationsList();

			case FOR_ROUTE:
				return DbProvider.getInstance().getStations().getStationsList(route);

			default:
				throw new LoaderException();
		}
	}
}
