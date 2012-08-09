package app.android.bustime.ui.loader;


import java.util.List;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import app.android.bustime.db.DbProvider;
import app.android.bustime.db.model.Route;
import app.android.bustime.db.model.Station;


public class RoutesLoader extends AsyncTaskLoader<List<Route>>
{
	private static enum Mode {
		LOAD_ALL, LOAD_FOR_STATION
	}

	private final Mode mode;

	private Station station;

	public RoutesLoader(Context context) {
		super(context);

		mode = Mode.LOAD_ALL;
	}

	public RoutesLoader(Context context, Station station) {
		super(context);

		this.station = station;

		mode = Mode.LOAD_FOR_STATION;
	}

	@Override
	protected void onStartLoading() {
		super.onStartLoading();

		forceLoad();
	}

	@Override
	public List<Route> loadInBackground() {
		switch (mode) {
			case LOAD_ALL:
				return DbProvider.getInstance().getRoutes().getRoutesList();

			case LOAD_FOR_STATION:
				return DbProvider.getInstance().getRoutes().getRoutesList(station);

			default:
				throw new LoaderException();
		}
	}
}
