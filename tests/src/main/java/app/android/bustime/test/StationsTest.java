package app.android.bustime.test;


import java.util.List;

import app.android.bustime.local.Route;
import app.android.bustime.local.Station;
import app.android.bustime.local.Time;


public class StationsTest extends DbTestCase
{
	public void testStationCreation() {
		Station station = stations.createStation(STATION_KALININA_NAME, STATION_KALININA_LATITUDE,
			STATION_KALININA_LONGITUDE);

		assertEquals(STATION_KALININA_NAME, station.getName());
		assertEquals(1, stations.getStationsList().size());
	}

	public void testStationDeleting() {
		Station station = stations.createStation(STATION_KALININA_NAME, STATION_KALININA_LATITUDE,
			STATION_KALININA_LONGITUDE);

		stations.deleteStation(station);

		assertEquals(0, stations.getStationsList().size());
	}

	public void testGetStationsList() {
		fillDatabaseWithStations();

		List<Station> stationsList = stations.getStationsList();

		assertNotNull(stationsList);
		assertEquals(STATIONS_COUNT, stationsList.size());
	}

	public void testGetStationsListByRoute() {
		fillDatabaseWithStations();

		Station station = stations.getStationsList().get(0);

		Route route = routes.createRoute(ROUTE_10_NAME);
		station.insertShiftTimeForRoute(route, new Time(0, 10));

		assertEquals(1, stations.getStationsList(route).size());
	}
}
