package ru.ming13.bustime.ui.loader;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import ru.ming13.bustime.db.DbProvider;
import ru.ming13.bustime.db.model.Station;


public class StationLoader extends AsyncTaskLoader<Station>
{
	private final long stationId;

	public StationLoader(Context context, long stationId) {
		super(context);

		this.stationId = stationId;
	}

	@Override
	protected void onStartLoading() {
		super.onStartLoading();

		forceLoad();
	}

	@Override
	public Station loadInBackground() {
		return DbProvider.getInstance().getStations().getStation(stationId);
	}
}
