package ru.ming13.bustime.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ru.ming13.bustime.util.Formatters;

public final class LocaleReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent == null) {
			return;
		}

		if (Intent.ACTION_LOCALE_CHANGED.equals(intent.getAction())) {
			Formatters.tearDownFormatters();
		}
	}
}
