package ru.ming13.bustime.bus;

import com.squareup.otto.Bus;

public final class BusProvider
{
	private BusProvider() {
	}

	private static final class BusHolder
	{
		public static final Bus BUS = new Bus();
	}

	public static Bus getBus() {
		return BusHolder.BUS;
	}
}
