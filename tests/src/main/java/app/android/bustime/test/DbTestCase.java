package app.android.bustime.test;


import android.test.AndroidTestCase;
import app.android.bustime.local.DbProvider;
import app.android.bustime.local.Route;
import app.android.bustime.local.Routes;
import app.android.bustime.local.Station;
import app.android.bustime.local.Stations;


public abstract class DbTestCase extends AndroidTestCase
{
	protected static final int ROUTES_COUNT = 2;
	protected static final String ROUTE_10_NAME = "10";
	protected static final String ROUTE_10A_NAME = "10A";

	protected static final int STATIONS_COUNT = 4;
	protected static final String STATION_KOSMOS_NAME = "Кинотеатр «Космос»";
	protected static final String STATION_MOLODEJNAYA_NAME = "Молодёжная";
	protected static final String STATION_KALININA_NAME = "Калинина";
	protected static final String STATION_KOPTEVO_NAME = "Коптево";
	protected static final double STATION_KOSMOS_LATITUDE = 55.539505;
	protected static final double STATION_KOSMOS_LONGITUDE = 28.636603;
	protected static final double STATION_MOLODEJNAYA_LATITUDE = 55.533081;
	protected static final double STATION_MOLODEJNAYA_LONGITUDE = 28.656033;
	protected static final double STATION_KALININA_LATITUDE = 55.534557;
	protected static final double STATION_KALININA_LONGITUDE = 28.661794;
	protected static final double STATION_KOPTEVO_LATITUDE = 55.539954;
	protected static final double STATION_KOPTEVO_LONGITUDE = 28.668349;

	protected Routes routes;
	protected Stations stations;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		DbProvider.getInstance(getContext());

		routes = DbProvider.getInstance().getRoutes();
		routes.beginTransaction();

		stations = DbProvider.getInstance().getStations();
		stations.beginTransaction();

		emptyDatabase();
	}

	private void emptyDatabase() {
		for (Route route : routes.getRoutesList()) {
			routes.deleteRoute(route);
		}

		for (Station station : stations.getStationsList()) {
			stations.deleteStation(station);
		}
	}

	protected void fillDatabaseWithRoutes() {
		routes.createRoute(ROUTE_10_NAME);
		routes.createRoute(ROUTE_10A_NAME);
	}

	protected void fillDatabaseWithStations() {
		stations.createStation(STATION_KOSMOS_NAME, STATION_KOSMOS_LATITUDE, STATION_KOSMOS_LONGITUDE);
		stations.createStation(STATION_MOLODEJNAYA_NAME, STATION_MOLODEJNAYA_LATITUDE,
			STATION_MOLODEJNAYA_LONGITUDE);
		stations.createStation(STATION_KALININA_NAME, STATION_KALININA_LATITUDE,
			STATION_KALININA_LONGITUDE);
		stations.createStation(STATION_KOPTEVO_NAME, STATION_KOPTEVO_LATITUDE,
			STATION_KOPTEVO_LONGITUDE);
	}

	protected void fillDatabase() {
		fillDatabaseWithRoutes();
		fillDatabaseWithStations();
	}

	@Override
	protected void tearDown() throws Exception {
		routes.endTransaction();
		stations.endTransaction();

		super.tearDown();
	}
}
