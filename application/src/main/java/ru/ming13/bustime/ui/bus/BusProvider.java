package ru.ming13.bustime.ui.bus;


import com.squareup.otto.Bus;


public final class BusProvider
{
	private BusProvider() {
	}

	private static final class BusHolder
	{
		public static final Bus BUS = new Bus();
	}

	public static Bus getInstance() {
		return BusHolder.BUS;
	}
}
