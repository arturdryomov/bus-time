package ru.ming13.bustime.ui.util;


import java.util.Calendar;

import android.os.Handler;


public class EveryMinuteActionPerformer
{
	public interface EveryMinuteCallback
	{
		public void onEveryMinute();
	}

	private static final int UPDATE_PERIOD_IN_MILLISECONDS = 60000;

	private final Handler timer;

	private final Runnable everyMinuteTask;

	public EveryMinuteActionPerformer(EveryMinuteCallback everyMinuteCallback) {
		this.everyMinuteTask = buildEveryMinuteTask(everyMinuteCallback);

		this.timer = new Handler();
	}

	private Runnable buildEveryMinuteTask(final EveryMinuteCallback everyMinuteCallback) {
		return new Runnable()
		{
			@Override
			public void run() {
				everyMinuteCallback.onEveryMinute();

				continuePerforming();
			}
		};
	}

	private void continuePerforming() {
		timer.postDelayed(everyMinuteTask, UPDATE_PERIOD_IN_MILLISECONDS);
	}

	public void startPerforming() {
		stopPerforming();

		timer.postDelayed(everyMinuteTask, calculateMillisecondsForNextMinute());
	}

	public void stopPerforming() {
		timer.removeCallbacks(everyMinuteTask);
	}

	private long calculateMillisecondsForNextMinute() {
		Calendar currentTime = Calendar.getInstance();

		Calendar nextMinuteTime = Calendar.getInstance();
		nextMinuteTime.add(Calendar.MINUTE, 1);
		nextMinuteTime.set(Calendar.SECOND, 0);

		return nextMinuteTime.getTimeInMillis() - currentTime.getTimeInMillis();
	}
}
