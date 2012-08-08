package app.android.bustime.ui;


import java.util.List;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import app.android.bustime.db.Route;
import app.android.bustime.db.Station;
import app.android.bustime.db.Time;


public class WorkdaysTimetableFragment extends TimetableFragment
{
	@Override
	protected AsyncTaskLoader<List<Time>> buildTimetableLoader() {
		return new TimetableLoader(getActivity(), station, route);
	}

	private static class TimetableLoader extends AsyncTaskLoader<List<Time>>
	{
		private final Station station;
		private final Route route;

		public TimetableLoader(Context context, Station station, Route route) {
			super(context);

			this.station = station;
			this.route = route;
		}

		@Override
		protected void onStartLoading() {
			super.onStartLoading();

			forceLoad();
		}

		@Override
		public List<Time> loadInBackground() {
			return station.getRouteWorkdaysDepartureTimetable(route);
		}
	}
}
