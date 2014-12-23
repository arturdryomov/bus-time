package ru.ming13.bustime.task;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import ru.ming13.bustime.bus.BusEvent;
import ru.ming13.bustime.bus.BusProvider;
import ru.ming13.bustime.bus.RoutePathLoadedEvent;
import ru.ming13.bustime.direction.Navigator;

public class RoutePathLoadingTask extends AsyncTask<Void, Void, BusEvent>
{
	private final Navigator navigator;

	private final List<LatLng> stopPositions;

	public static void execute(@NonNull List<LatLng> stopPositions) {
		new RoutePathLoadingTask(stopPositions).execute();
	}

	private RoutePathLoadingTask(List<LatLng> stopPositions) {
		this.navigator = new Navigator();

		this.stopPositions = stopPositions;
	}

	@Override
	protected BusEvent doInBackground(Void... voids) {
		return new RoutePathLoadedEvent(getPathPositions());
	}

	private List<LatLng> getPathPositions() {
		List<LatLng> positions = new ArrayList<>();

		for (List<LatLng> positionsPartition : partitionPositions(stopPositions, getPositionsPartitionSize())) {
			LatLng originPosition = positionsPartition.get(0);
			LatLng destinationPosition = positionsPartition.get(positionsPartition.size() - 1);
			List<LatLng> waypointPositions = positionsPartition.subList(1, positionsPartition.size() - 1);

			positions.addAll(navigator.getDirectionPolylinePositions(originPosition, destinationPosition, waypointPositions));
		}

		return positions;
	}

	private List<List<LatLng>> partitionPositions(List<LatLng> positions, int partitionSize) {
		List<List<LatLng>> partitions = new ArrayList<>();

		int partitionStart = 0;

		while (true) {
			int partitionFinish = partitionStart + partitionSize;

			if (partitionFinish < positions.size()) {
				partitions.add(positions.subList(partitionStart, partitionFinish));
			} else {
				partitions.add(positions.subList(partitionStart, positions.size()));
				break;
			}

			// Partitions should intersect to keep the route continuous

			partitionStart += partitionSize - 1;
		}

		return partitions;
	}

	private int getPositionsPartitionSize() {
		// Origin + destination + waypoints

		return 1 + 1 + Navigator.Constraints.WAYPOINTS_COUNT;
	}

	@Override
	protected void onPostExecute(BusEvent busEvent) {
		super.onPostExecute(busEvent);

		BusProvider.getBus().post(busEvent);
	}
}
