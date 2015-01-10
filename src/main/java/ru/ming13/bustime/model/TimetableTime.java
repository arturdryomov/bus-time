package ru.ming13.bustime.model;

import ru.ming13.bustime.util.Time;

public class TimetableTime
{
	private final Time time;

	public TimetableTime(Time time) {
		this.time = time;
	}

	public Time getTime() {
		return time;
	}
}
