package ru.ming13.bustime.util;

import android.os.Handler;

import java.util.Calendar;

import ru.ming13.bustime.bus.BusProvider;
import ru.ming13.bustime.bus.TimeChangedEvent;

public final class Timer implements Runnable
{
	private static final int UPDATE_PERIOD_IN_MILLIS = 1 * 60 * 1000;

	private final Handler timerHandler;

	public Timer() {
		timerHandler = new Handler();
	}

	public void start() {
		stop();

		postponeMessage(calculateMillisForNextMinute());
	}

	public void stop() {
		timerHandler.removeCallbacks(this);
	}

	private void postponeMessage(long postponeMillis) {
		timerHandler.postDelayed(this, postponeMillis);
	}

	private long calculateMillisForNextMinute() {
		Calendar currentTime = Calendar.getInstance();

		Calendar nextMinuteTime = Calendar.getInstance();
		nextMinuteTime.add(Calendar.MINUTE, 1);
		nextMinuteTime.set(Calendar.SECOND, 0);
		nextMinuteTime.set(Calendar.MILLISECOND, 0);

		return nextMinuteTime.getTimeInMillis() - currentTime.getTimeInMillis();
	}

	@Override
	public void run() {
		sendMessage();

		postponeMessage(UPDATE_PERIOD_IN_MILLIS);
	}

	private void sendMessage() {
		BusProvider.getBus().post(new TimeChangedEvent());
	}
}
