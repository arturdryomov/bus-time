package ru.ming13.bustime.bus;

public class TimetableInformationQueriedEvent implements BusEvent
{
	private final int timetableType;
	private final int timetableClosestTripPosition;

	public TimetableInformationQueriedEvent(int timetableType, int timetableClosestTripPosition) {
		this.timetableType = timetableType;
		this.timetableClosestTripPosition = timetableClosestTripPosition;
	}

	public int getTimetableType() {
		return timetableType;
	}

	public int getTimetableClosestTripPosition() {
		return timetableClosestTripPosition;
	}
}
