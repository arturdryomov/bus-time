package ru.ming13.bustime.model;

import ru.ming13.bustime.util.Time;

public final class StopRoute
{
	private final Route route;
	private final Time routeTime;

	public StopRoute(Route route, Time routeTime) {
		this.route = route;
		this.routeTime = routeTime;
	}

	public Route getRoute() {
		return route;
	}

	public Time getRouteTime() {
		return routeTime;
	}
}
