package ru.ming13.bustime.ui.loader;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import ru.ming13.bustime.db.model.Route;


public class TimetableTypeCheckLoader extends AsyncTaskLoader<Bundle>
{
	public static final String RESULT_TIMETABLE_WEEK_PART_DEPENDENT_KEY = "week_part_dependent";

	private final Route route;

	public TimetableTypeCheckLoader(Context context, Route route) {
		super(context);

		this.route = route;
	}

	@Override
	protected void onStartLoading() {
		super.onStartLoading();

		forceLoad();
	}

	@Override
	public Bundle loadInBackground() {
		Bundle result = new Bundle();

		result.putBoolean(RESULT_TIMETABLE_WEEK_PART_DEPENDENT_KEY, route.isWeekPartDependent());

		return result;
	}
}
