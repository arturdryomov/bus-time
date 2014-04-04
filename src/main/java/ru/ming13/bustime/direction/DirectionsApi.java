package ru.ming13.bustime.direction;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;

interface DirectionsApi
{
	@FormUrlEncoded
	@GET("/directions/json")
	DirectionsInformation getDirectionsInformation(@Field("origin") String originPosition, @Field("destination") String destinationPosition, @Field("waypoints") String waypointPositions, @Field("sensor") boolean isSensorEnabled);
}
