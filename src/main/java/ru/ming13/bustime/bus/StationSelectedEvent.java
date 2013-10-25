package ru.ming13.bustime.bus;

public class StationSelectedEvent implements BusEvent
{
	private final long stationId;
	private final String stationName;
	private final String stationDirection;

	public StationSelectedEvent(long stationId, String stationName, String stationDirection) {
		this.stationId = stationId;
		this.stationName = stationName;
		this.stationDirection = stationDirection;
	}

	public long getStationId() {
		return stationId;
	}

	public String getStationName() {
		return stationName;
	}

	public String getStationDirection() {
		return stationDirection;
	}
}
