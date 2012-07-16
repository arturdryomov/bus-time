package app.android.bustime.ui;


import app.android.bustime.db.Station;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;


public class StationOverlayItem extends OverlayItem
{
	private Station station;

	public StationOverlayItem(GeoPoint geoPoint, Station station) {
		super(geoPoint, station.getName(), null);

		this.station = station;
	}

	public Station getStation() {
		return station;
	}
}
