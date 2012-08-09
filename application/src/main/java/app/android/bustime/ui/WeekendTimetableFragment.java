package app.android.bustime.ui;


import java.util.List;

import android.support.v4.content.AsyncTaskLoader;
import app.android.bustime.db.Time;
import app.android.bustime.ui.loader.TimetableLoader;


public class WeekendTimetableFragment extends TimetableFragment
{
	@Override
	protected AsyncTaskLoader<List<Time>> buildTimetableLoader() {
		return TimetableLoader.buildWeekendLoader(getActivity(), route, station);
	}
}
