package ru.ming13.bustime.ui.intent;


public final class IntentExtras
{
	private IntentExtras() {
	}

	public static final String ROUTE;
	public static final String STATION;
	public static final String STATION_ID;

	private static final String EXTRA_PREFIX = IntentFactory.class.getPackage().getName();

	private static final String ROUTE_POSTFIX = "route";
	private static final String STATION_POSTFIX = "station";
	private static final String STATION_ID_POSTFIX = "station_id";

	static {
		ROUTE = String.format("%s.%s", EXTRA_PREFIX, ROUTE_POSTFIX);
		STATION = String.format("%s.%s", EXTRA_PREFIX, STATION_POSTFIX);
		STATION_ID = String.format("%s.%s", EXTRA_PREFIX, STATION_ID_POSTFIX);
	}
}
