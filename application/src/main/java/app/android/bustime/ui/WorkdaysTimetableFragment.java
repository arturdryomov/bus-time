package app.android.bustime.ui;


public class WorkdaysTimetableFragment extends TimetableFragment
{
	@Override
	protected LoadTimetableTask buildLoadTimetableTask() {
		return new LoadWorkdaysTimetableTask();
	}

	private class LoadWorkdaysTimetableTask extends LoadTimetableTask
	{
		@Override
		protected Void doInBackground(Void... parameters) {
			timetable = station.getRouteWorkdaysDepartureTimetable(route);

			return null;
		}
	}
}
