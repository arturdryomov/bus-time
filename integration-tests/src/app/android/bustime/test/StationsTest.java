package app.android.bustime.test;


import java.util.List;

import app.android.bustime.local.Station;


public class StationsTest extends DbTestCase
{
	public void testGetStationsList() {
		fillDatabaseWithStations();
		
		List<Station> stationsList = stations.getStationsList();
		
		assertNotNull(stationsList);
		assertEquals(STATIONS_COUNT, stationsList.size());
	}
	
	public void testStationCreation() {
		Station station = stations.createStation(STATION_KALININA_NAME);
		
		assertEquals(STATION_KALININA_NAME, station.getName());
		assertEquals(1, stations.getStationsList().size());
	}
	
	public void testStationDeleting() {
		Station station = stations.createStation(STATION_KALININA_NAME);
		
		stations.deleteStation(station);
		
		assertEquals(0, stations.getStationsList().size());
	}
}
