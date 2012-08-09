package ru.ming13.bustime.ui.map;


import ru.ming13.bustime.db.model.Station;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;


public class StationOverlayItem extends OverlayItem
{
	private final Station station;

	public StationOverlayItem(Station station, GeoPoint geoPoint) {
		super(geoPoint, station.getName(), null);

		this.station = station;
	}

	public StationOverlayItem(Station station, GeoPoint geoPoint, String title, String remark) {
		super(geoPoint, title, remark);

		this.station = station;
	}

	public Station getStation() {
		return station;
	}
}
