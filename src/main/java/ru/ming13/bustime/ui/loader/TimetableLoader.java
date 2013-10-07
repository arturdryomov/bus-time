package ru.ming13.bustime.ui.loader;


import java.util.List;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import ru.ming13.bustime.db.model.Route;
import ru.ming13.bustime.db.model.Station;
import ru.ming13.bustime.db.time.Time;


public class TimetableLoader extends AsyncTaskLoader<List<Time>>
{
	private static enum Mode
	{
		FULL_WEEK, WORKDAYS, WEEKEND
	}

	private final Route route;
	private final Station station;
	private final Mode mode;

	public static TimetableLoader newFullWeekLoader(Context context, Route route, Station station) {
		return new TimetableLoader(context, route, station, Mode.FULL_WEEK);
	}

	private TimetableLoader(Context context, Route route, Station station, Mode mode) {
		super(context);

		this.route = route;
		this.station = station;
		this.mode = mode;
	}

	public static TimetableLoader newWorkdaysLoader(Context context, Route route, Station station) {
		return new TimetableLoader(context, route, station, Mode.WORKDAYS);
	}

	public static TimetableLoader newWeekendLoader(Context context, Route route, Station station) {
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
				return station.getFullWeekTimetable(route);

			case WORKDAYS:
				return station.getWorkdaysTimetable(route);

			case WEEKEND:
				return station.getWeekendTimetable(route);

			default:
				throw new LoaderException();
		}
	}
}
