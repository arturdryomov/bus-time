package ru.ming13.bustime.bus;

public class TimetableTypeQueriedEvent implements BusEvent
{
	private final int timetableType;

	public TimetableTypeQueriedEvent(int timetableType) {
		this.timetableType = timetableType;
	}

	public int getTimetableType() {
		return timetableType;
	}
}
