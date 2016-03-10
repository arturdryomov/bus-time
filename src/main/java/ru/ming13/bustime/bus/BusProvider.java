package ru.ming13.bustime.bus;

import android.support.annotation.NonNull;

import com.squareup.otto.Bus;

public final class BusProvider
{
	private static final class BusHolder
	{
		public static final Bus BUS = new Bus();
	}

	private BusProvider() {
	}

	@NonNull
	public static Bus getBus() {
		return BusHolder.BUS;
	}
}
