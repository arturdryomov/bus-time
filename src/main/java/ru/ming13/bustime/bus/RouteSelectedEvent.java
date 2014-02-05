package ru.ming13.bustime.bus;

import ru.ming13.bustime.model.Route;

public class RouteSelectedEvent implements BusEvent
{
	private final Route route;

	public RouteSelectedEvent(Route route) {
		this.route = route;
	}

	public Route getRoute() {
		return route;
	}
}
