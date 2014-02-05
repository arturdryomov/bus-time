package ru.ming13.bustime.bus;

public class StopSelectedEvent implements BusEvent
{
	private final long stopId;
	private final String stopName;
	private final String stopDirection;

	public StopSelectedEvent(long stopId, String stopName, String stopDirection) {
		this.stopId = stopId;
		this.stopName = stopName;
		this.stopDirection = stopDirection;
	}

	public long getStopId() {
		return stopId;
	}

	public String getStopName() {
		return stopName;
	}

	public String getStopDirection() {
		return stopDirection;
	}
}
