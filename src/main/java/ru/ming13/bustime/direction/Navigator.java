package ru.ming13.bustime.direction;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import ru.ming13.bustime.util.Strings;

public final class Navigator
{
	public static final class Constraints
	{
		private Constraints() {
		}

		public static final int WAYPOINT_COUNT = 8;
	}

	private static final class Urls
	{
		private Urls() {
		}

		public static final String ENDPOINT = "https://maps.googleapis.com/maps/api";
	}

	private final DirectionsApi directionsApi;

	public Navigator() {
		this.directionsApi = createDirectionsApi();
	}

	private DirectionsApi createDirectionsApi() {
		RestAdapter directionsAdapter = new RestAdapter.Builder()
			.setEndpoint(Urls.ENDPOINT)
			.build();

		return directionsAdapter.create(DirectionsApi.class);
	}

	@NonNull
	public List<LatLng> getDirectionPolylinePositions(@NonNull LatLng originPosition, @NonNull LatLng destinationPosition, @NonNull List<LatLng> waypointPositions) {
		try {
			DirectionsInformation directionsInformation = directionsApi.getDirectionsInformation(
				formatPosition(originPosition),
				formatPosition(destinationPosition),
				formatPositions(waypointPositions));

			return parseDirectionPolylinePositions(directionsInformation);
		} catch (RetrofitError e) {
			return new ArrayList<>();
		}
	}

	private String formatPosition(LatLng position) {
		return String.format(Locale.US, "%f,%f", position.latitude, position.longitude);
	}

	private String formatPositions(List<LatLng> positions) {
		List<String> formattedPositions = new ArrayList<>();

		for (LatLng position : positions) {
			formattedPositions.add(formatPosition(position));
		}

		return Strings.join(formattedPositions, "|");
	}

	private List<LatLng> parseDirectionPolylinePositions(DirectionsInformation directionsInformation) {
		if (!directionsInformation.isEmpty()) {
			final int routePosition = 0;

			return PolyUtil.decode(directionsInformation.getPolylinePositions(routePosition));
		} else {
			return new ArrayList<>();
		}
	}
}
