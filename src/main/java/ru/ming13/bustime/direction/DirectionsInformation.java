package ru.ming13.bustime.direction;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public final class DirectionsInformation
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

	public boolean isEmpty() {
		return routes.isEmpty();
	}

	@NonNull
	public String getPolylinePositions(int routePosition) {
		return routes.get(routePosition).getPolyline().getPositions();
	}
}
