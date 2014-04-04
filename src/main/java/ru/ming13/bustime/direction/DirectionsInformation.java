package ru.ming13.bustime.direction;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DirectionsInformation
{
	private static class Route
	{
		@SerializedName("overview_polyline")
		private Polyline polyline;

		public Polyline getPolyline() {
			return polyline;
		}
	}

	private static class Polyline
	{
		@SerializedName("points")
		private String positions;

		public String getPositions() {
			return positions;
		}
	}

	@SerializedName("routes")
	private List<Route> routes;

	public List<String> getPolylinesPositions() {
		List<String> polylinesPositions = new ArrayList<String>();

		for (Route route : routes) {
			polylinesPositions.add(route.getPolyline().getPositions());
		}

		return polylinesPositions;
	}
}
