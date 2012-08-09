package ru.ming13.bustime.ui.util;


import android.os.Handler;


public final class LoaderSafeRunner
{
	private LoaderSafeRunner() {
	}

	public static void run(Runnable runnable) {
		Handler handler = new Handler();

		handler.post(runnable);
	}
}
