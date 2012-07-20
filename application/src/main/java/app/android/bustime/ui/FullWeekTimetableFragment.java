package app.android.bustime.ui;


public class FullWeekTimetableFragment extends TimetableFragment
{
	@Override
	protected LoadTimetableTask buildLoadTimetableTask() {
		return new LoadFullWeekTimetableTask();
	}

	private class LoadFullWeekTimetableTask extends LoadTimetableTask
	{
		@Override
		protected Void doInBackground(Void... parameters) {
			timetable = station.getRouteFullWeekTimetable(route);

			return null;
		}
	}
}
