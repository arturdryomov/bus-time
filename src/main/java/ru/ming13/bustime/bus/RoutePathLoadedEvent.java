package ru.ming13.bustime.bus;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class RoutePathLoadedEvent implements BusEvent
{
	private final List<LatLng> pathPositions;

	public RoutePathLoadedEvent(List<LatLng> pathPositions) {
		this.pathPositions = pathPositions;
	}

	public List<LatLng> getPathPositions() {
		return pathPositions;
	}
}
