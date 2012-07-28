package app.android.bustime.ui;


import java.util.List;

import app.android.bustime.db.Time;


public class FullWeekTimetableFragment extends TimetableFragment
{
	@Override
	protected List<Time> buildTimetable() {
		return station.getRouteFullWeekTimetable(route);
	}
}
