package ru.ming13.bustime.cursor;

import android.database.Cursor;

import com.venmo.cursor.IterableCursorWrapper;

import ru.ming13.bustime.model.Stop;
import ru.ming13.bustime.provider.BusTimeContract;

public class StopsCursor extends IterableCursorWrapper<Stop>
{
	public StopsCursor(Cursor cursor) {
		super(cursor);
	}

	@Override
	public Stop peek() {
		long stopId = getLong(BusTimeContract.Stops._ID, CursorDefaults.LONG);
		String stopName = getString(BusTimeContract.Stops.NAME, CursorDefaults.STRING);
		String stopDirection = getString(BusTimeContract.Stops.DIRECTION, CursorDefaults.STRING);
		double stopLatitude = getDouble(BusTimeContract.Stops.LATITUDE, CursorDefaults.DOUBLE);
		double stopLongitude = getDouble(BusTimeContract.Stops.LONGITUDE, CursorDefaults.DOUBLE);

		return new Stop(stopId, stopName, stopDirection, stopLatitude, stopLongitude);
	}
}
