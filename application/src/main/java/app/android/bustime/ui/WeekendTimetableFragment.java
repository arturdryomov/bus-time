package app.android.bustime.ui;


public class WeekendTimetableFragment extends TimetableFragment
{
	@Override
	protected LoadTimetableTask buildLoadTimetableTask() {
		return new LoadWeekendTimetableTask();
	}

	private class LoadWeekendTimetableTask extends LoadTimetableTask
	{
		@Override
		protected Void doInBackground(Void... parameters) {
			timetable = station.getRouteWeekendDepartureTimetable(route);

			return null;
		}
	}
}
