package ru.ming13.bustime.ui.util;


import android.os.Handler;

/*
 * AsyncTaskLoaders couldn’t call FragmentTransactions from onLoadFinished() method.
 * Solution is move actions to the handler. Ugly but Android developers think that UI shouldn’t
 * change on application run.
 */

public final class LoaderSafeRunner
{
	private LoaderSafeRunner() {
	}

	public static void run(Runnable runnable) {
		Handler handler = new Handler();

		handler.post(runnable);
	}
}
