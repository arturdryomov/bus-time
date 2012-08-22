package ru.ming13.bustime.ui.loader;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import ru.ming13.bustime.db.model.Route;


public class TimetableTypeCheckLoader extends AsyncTaskLoader<Boolean>
{
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
	public Boolean loadInBackground() {
		return Boolean.valueOf(route.isWeekPartDependent());
	}
}
