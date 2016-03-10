package ru.ming13.bustime.cursor;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.venmo.cursor.IterableCursorWrapper;

import ru.ming13.bustime.model.Route;
import ru.ming13.bustime.model.StopRoute;
import ru.ming13.bustime.provider.BusTimeContract;
import ru.ming13.bustime.util.Time;

public final class StopRoutesCursor extends IterableCursorWrapper<StopRoute>
{
	public StopRoutesCursor(@NonNull Cursor cursor) {
		super(cursor);
	}

	@Override
	public StopRoute peek() {
		long routeId = getLong(BusTimeContract.Routes._ID, CursorDefaults.LONG);
		String routeNumber = getString(BusTimeContract.Routes.NUMBER, CursorDefaults.STRING);
		String routeDescription = getString(BusTimeContract.Routes.DESCRIPTION, CursorDefaults.STRING);
		Time routeTime = Time.from(getString(BusTimeContract.Timetable.ARRIVAL_TIME, CursorDefaults.STRING));

		return new StopRoute(new Route(routeId, routeNumber, routeDescription), routeTime);
	}
}
