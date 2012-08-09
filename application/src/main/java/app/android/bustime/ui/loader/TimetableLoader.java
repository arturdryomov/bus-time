package app.android.bustime.ui.loader;


import java.util.List;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import app.android.bustime.db.Route;
import app.android.bustime.db.Station;
import app.android.bustime.db.Time;


public class TimetableLoader extends AsyncTaskLoader<List<Time>>
{
	private static enum Mode
	{
		FULL_WEEK, WORKDAYS, WEEKEND
	}

	private final Route route;
	private final Station station;
	private final Mode mode;

	private TimetableLoader(Context context, Route route, Station station, Mode mode) {
		super(context);

		this.route = route;
		this.station = station;
		this.mode = mode;
	}

	public static TimetableLoader buildFullWeekLoader(Context context, Route route, Station station) {
		return new TimetableLoader(context, route, station, Mode.FULL_WEEK);
	}

	public static TimetableLoader buildWorkdaysLoader(Context context, Route route, Station station) {
		return new TimetableLoader(context, route, station, Mode.WORKDAYS);
	}

	public static TimetableLoader buildWeekendLoader(Context context, Route route, Station station) {
		return new TimetableLoader(context, route, station, Mode.WEEKEND);
	}

	@Override
	protected void onStartLoading() {
		super.onStartLoading();

		forceLoad();
	}

	@Override
	public List<Time> loadInBackground() {
		switch (mode) {
			case FULL_WEEK:
				return station.getRouteFullWeekTimetable(route);

			case WORKDAYS:
				// TODO: Rename to getWorkdaysDepartureTimetable (and other methods similar way)
				return station.getRouteWorkdaysDepartureTimetable(route);

			case WEEKEND:
				return station.getRouteWeekendDepartureTimetable(route);

			default:
				throw new LoaderException();
		}
	}
}
