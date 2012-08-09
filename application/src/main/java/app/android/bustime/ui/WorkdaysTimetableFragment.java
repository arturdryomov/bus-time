package app.android.bustime.ui;


import java.util.List;

import android.support.v4.content.AsyncTaskLoader;
import app.android.bustime.db.Time;
import app.android.bustime.ui.loader.TimetableLoader;


public class WorkdaysTimetableFragment extends TimetableFragment
{
	@Override
	protected AsyncTaskLoader<List<Time>> buildTimetableLoader() {
		return TimetableLoader.buildWorkdaysLoader(getActivity(), route, station);
	}
}
