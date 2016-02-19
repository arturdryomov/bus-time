package ru.ming13.bustime.bus;

import ru.ming13.bustime.model.Stop;

public final class StopLoadedEvent implements BusEvent
{
	private final Stop stop;

	public StopLoadedEvent(Stop stop) {
		this.stop = stop;
	}

	public Stop getStop() {
		return stop;
	}
}
