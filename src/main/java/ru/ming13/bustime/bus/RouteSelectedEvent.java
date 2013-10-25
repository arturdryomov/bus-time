package ru.ming13.bustime.bus;

public class RouteSelectedEvent implements BusEvent
{
	private final long routeId;
	private final String routeNumber;
	private final String routeDescription;

	public RouteSelectedEvent(long routeId, String routeNumber, String routeDescription) {
		this.routeId = routeId;
		this.routeNumber = routeNumber;
		this.routeDescription = routeDescription;
	}

	public long getRouteId() {
		return routeId;
	}

	public String getRouteNumber() {
		return routeNumber;
	}

	public String getRouteDescription() {
		return routeDescription;
	}
}
