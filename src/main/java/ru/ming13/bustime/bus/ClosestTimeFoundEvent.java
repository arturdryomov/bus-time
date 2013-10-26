package ru.ming13.bustime.bus;

public class ClosestTimeFoundEvent implements BusEvent
{
	private final int closestTimePosition;

	public ClosestTimeFoundEvent(int closestTimePosition) {
		this.closestTimePosition = closestTimePosition;
	}

	public int getClosestTimePosition() {
		return closestTimePosition;
	}
}
