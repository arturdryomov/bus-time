package ru.ming13.bustime.bus;

public class TimetableInformationLoadedEvent implements BusEvent
{
	private final int timetableType;
	private final int timetableClosestTripPosition;

	public TimetableInformationLoadedEvent(int timetableType, int timetableClosestTripPosition) {
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
