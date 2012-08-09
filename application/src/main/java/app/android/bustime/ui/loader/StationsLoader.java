package app.android.bustime.ui.loader;


import java.util.List;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import app.android.bustime.db.DbProvider;
import app.android.bustime.db.Route;
import app.android.bustime.db.Station;


public class StationsLoader extends AsyncTaskLoader<List<Station>>
{
	private static enum Mode {
		LOAD_ALL, LOAD_FOR_ROUTE
	}

	private final Mode mode;

	private Route route;

	public StationsLoader(Context context) {
		super(context);

		mode = Mode.LOAD_ALL;
	}

	public StationsLoader(Context context, Route route) {
		super(context);

		this.route = route;

		mode = Mode.LOAD_FOR_ROUTE;
	}

	@Override
	protected void onStartLoading() {
		super.onStartLoading();

		forceLoad();
	}

	@Override
	public List<Station> loadInBackground() {
		switch (mode) {
			case LOAD_ALL:
				return DbProvider.getInstance().getStations().getStationsList();

			case LOAD_FOR_ROUTE:
				return DbProvider.getInstance().getStations().getStationsList(route);

			default:
				throw new LoaderException();
		}
	}
}
