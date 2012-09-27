package ru.ming13.bustime.ui.loader;


import java.util.List;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import ru.ming13.bustime.db.DbProvider;
import ru.ming13.bustime.db.model.Route;


public class RoutesLoader extends AsyncTaskLoader<List<Route>>
{
	public RoutesLoader(Context context) {
		super(context);
	}

	@Override
	protected void onStartLoading() {
		super.onStartLoading();

		forceLoad();
	}

	@Override
	public List<Route> loadInBackground() {
		return DbProvider.getInstance().getRoutes().getRoutesList();
	}
}
