package ru.ming13.bustime.cursor;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.venmo.cursor.IterableCursorWrapper;

import ru.ming13.bustime.model.TimetableTime;
import ru.ming13.bustime.provider.BusTimeContract;
import ru.ming13.bustime.util.Time;

public final class TimetableCursor extends IterableCursorWrapper<TimetableTime>
{
	public TimetableCursor(@NonNull Cursor cursor) {
		super(cursor);
	}

	@Override
	public TimetableTime peek() {
		Time timetableTime = Time.from(getString(BusTimeContract.Timetable.ARRIVAL_TIME, CursorDefaults.STRING));

		return new TimetableTime(timetableTime);
	}
}
