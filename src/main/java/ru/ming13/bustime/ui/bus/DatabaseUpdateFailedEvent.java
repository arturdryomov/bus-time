package ru.ming13.bustime.ui.bus;


public class DatabaseUpdateFailedEvent implements BusEvent
{
	private final boolean isNetworkRelated;

	public DatabaseUpdateFailedEvent(boolean isNetworkRelatedProblem) {
		this.isNetworkRelated = isNetworkRelatedProblem;
	}

	public boolean isNetworkRelatedProblem() {
		return isNetworkRelated;
	}
}
