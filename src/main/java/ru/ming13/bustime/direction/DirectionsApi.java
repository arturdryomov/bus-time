package ru.ming13.bustime.direction;

import retrofit.http.GET;
import retrofit.http.Query;

interface DirectionsApi
{
	@GET("/directions/json?sensor=false")
	DirectionsInformation getDirectionsInformation(
		@Query("origin") String originPosition,
		@Query("destination") String destinationPosition,
		@Query("waypoints") String waypointPositions);
}
