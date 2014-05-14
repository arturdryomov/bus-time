package ru.ming13.bustime.bus;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class RouteDirectionLoadedEvent implements BusEvent
{
	private final List<List<LatLng>> directionPartitions;

	public RouteDirectionLoadedEvent(List<List<LatLng>> directionPartitions) {
		this.directionPartitions = directionPartitions;
	}

	public List<List<LatLng>> getDirectionPartitions() {
		return directionPartitions;
	}
}
