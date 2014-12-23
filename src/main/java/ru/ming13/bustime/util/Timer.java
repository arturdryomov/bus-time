package ru.ming13.bustime.util;

import android.os.Handler;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import ru.ming13.bustime.bus.BusProvider;
import ru.ming13.bustime.bus.TimeChangedEvent;

public final class Timer implements Runnable
{
	private static final int UPDATE_PERIOD_IN_MINUTES = 1;

	private final Handler timerSchedule;

	public Timer() {
		timerSchedule = new Handler();
	}

	public void start() {
		stop();

		schedule(calculateMillisForNextMinute());
	}

	public void stop() {
		timerSchedule.removeCallbacks(this);
	}

	private void schedule(long scheduleMillis) {
		timerSchedule.postDelayed(this, scheduleMillis);
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
		sendNotification();

		schedule(TimeUnit.MINUTES.toMillis(UPDATE_PERIOD_IN_MINUTES));
	}

	private void sendNotification() {
		BusProvider.getBus().post(new TimeChangedEvent());
	}
}
