package ru.ming13.bustime.cursor;

import android.database.Cursor;

import com.venmo.cursor.IterableCursorWrapper;

import ru.ming13.bustime.model.Route;
import ru.ming13.bustime.provider.BusTimeContract;

public class RoutesCursor extends IterableCursorWrapper<Route>
{
	public RoutesCursor(Cursor cursor) {
		super(cursor);
	}

	@Override
	public Route peek() {
		long routeId = getLong(BusTimeContract.Routes._ID, CursorDefaults.LONG);
		String routeNumber = getString(BusTimeContract.Routes.NUMBER, CursorDefaults.STRING);
		String routeDescription = getString(BusTimeContract.Routes.DESCRIPTION, CursorDefaults.STRING);

		return new Route(routeId, routeNumber, routeDescription);
	}
}
