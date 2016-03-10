package ru.ming13.bustime.bus;

import android.support.annotation.NonNull;

import com.squareup.otto.DeadEvent;
import com.squareup.otto.Subscribe;

import java.util.HashSet;
import java.util.Set;

public final class BusEventsCollector
{
	private static final class CollectorHolder
	{
		public static final BusEventsCollector COLLECTOR = new BusEventsCollector();
	}

	private final Set<BusEvent> events;

	private BusEventsCollector() {
		this.events = new HashSet<>();
	}

	@NonNull
	public static BusEventsCollector getInstance() {
		return CollectorHolder.COLLECTOR;
	}

	public void postCollectedEvents() {
		for (BusEvent event : events) {
			BusProvider.getBus().post(event);
		}

		events.clear();
	}

	@Subscribe
	public void onDeadEvent(DeadEvent deadEvent) {
		events.add((BusEvent) deadEvent.event);
	}
}
