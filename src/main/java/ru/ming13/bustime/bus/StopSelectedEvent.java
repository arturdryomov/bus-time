package ru.ming13.bustime.bus;

import ru.ming13.bustime.model.Stop;

public class StopSelectedEvent implements BusEvent
{
	private final Stop stop;

	public StopSelectedEvent(Stop stop) {
		this.stop = stop;
	}

	public Stop getStop() {
		return stop;
	}
}
