package ru.ming13.bustime.ui.bus;


import java.util.HashSet;
import java.util.Set;

import com.squareup.otto.DeadEvent;
import com.squareup.otto.Subscribe;


public final class BusEventsCollector
{
	private final Set<BusEvent> events;

	private BusEventsCollector() {
		this.events = new HashSet<BusEvent>();
	}

	private static final class CollectorHolder
	{
		public static final BusEventsCollector COLLECTOR = new BusEventsCollector();
	}

	public static BusEventsCollector getInstance() {
		return CollectorHolder.COLLECTOR;
	}

	@Subscribe
	public void onEventWithoutSubscribers(DeadEvent deadEvent) {
		BusEvent eventWithoutSubscribers = (BusEvent) deadEvent.event;

		events.add(eventWithoutSubscribers);
	}

	public void postCollectedEvents() {
		for (BusEvent event : events) {
			BusProvider.getInstance().post(event);
		}

		events.clear();
	}
}