package ru.ming13.bustime.direction;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit.RestAdapter;
import retrofit.RetrofitError;

public class Navigator
{
	public static final class Constraints
	{
		private Constraints() {
		}

		public static final int WAYPOINTS_COUNT = 8;
	}

	private static final String MAPS_URL = "https://maps.googleapis.com/maps/api";

	private final DirectionsApi directionsApi;

	public Navigator() {
		this.directionsApi = buildDirectionsApi();
	}

	private DirectionsApi buildDirectionsApi() {
		RestAdapter directionsAdapter = new RestAdapter.Builder()
			.setEndpoint(MAPS_URL)
			.build();

		return directionsAdapter.create(DirectionsApi.class);
	}

	public List<LatLng> getDirectionPolylinePositions(@NonNull LatLng originPosition, @NonNull LatLng destinationPosition, @NonNull List<LatLng> waypointPositions) {
		try {
			DirectionsInformation directionsInformation = directionsApi.getDirectionsInformation(
				formatPosition(originPosition),
				formatPosition(destinationPosition),
				formatPositions(waypointPositions));

			return parseDirectionPolylinePositions(directionsInformation);
		} catch (RetrofitError e) {
			return new ArrayList<LatLng>();
		}
	}

	private String formatPosition(LatLng position) {
		return String.format(Locale.US, "%f,%f", position.latitude, position.longitude);
	}

	private String formatPositions(List<LatLng> positions) {
		List<String> formattedPositions = new ArrayList<String>();

		for (LatLng position : positions) {
			formattedPositions.add(formatPosition(position));
		}

		return StringUtils.join(formattedPositions, "|");
	}

	private List<LatLng> parseDirectionPolylinePositions(DirectionsInformation directionsInformation) {
		final int routePosition = 0;

		return PolyUtil.decode(directionsInformation.getPolylinePositions(routePosition));
	}
}
