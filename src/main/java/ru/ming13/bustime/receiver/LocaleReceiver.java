package ru.ming13.bustime.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ru.ming13.bustime.util.Formatters;

public class LocaleReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_LOCALE_CHANGED)) {
			handleLocaleChangeEvent();
		}
	}

	private void handleLocaleChangeEvent() {
		Formatters.tearDownFormatters();
	}
}
