package app.android.bustime.ui;


import android.content.Context;
import app.android.bustime.R;
import app.android.bustime.local.Time;


public class HumanTimeFormatter
{
	private final static int MINUTE_SINGULAR_FORM_CASE = 1;
	private final static int HOUR_SINGULAR_FORM_CASE = 1;

	private final Context context;

	public HumanTimeFormatter(Context context) {
		this.context = context;
	}

	public String toHumanFormat(Time time) {
		StringBuilder humanTimeBuilder = new StringBuilder();

		final int hours = time.getHours();
		final int minutes = time.getMinutes();

		if (hours != 0) {
			humanTimeBuilder.append(String.format("%d ", hours));
			humanTimeBuilder.append(getHourPostfix(hours));
		}

		if ((hours != 0) && (minutes != 0)) {
			humanTimeBuilder.append(" ");
		}

		if (minutes != 0) {
			humanTimeBuilder.append(String.format("%d ", minutes));
			humanTimeBuilder.append(getMinutePostfix(minutes));
		}

		return humanTimeBuilder.toString();
	}

	private String getMinutePostfix(int minutes) {
		if (minutes == MINUTE_SINGULAR_FORM_CASE) {
			return context.getString(R.string.token_time_minute_singular);
		}
		else {
			return context.getString(R.string.token_time_minute_plural);
		}
	}

	private String getHourPostfix(int hours) {
		if (hours == HOUR_SINGULAR_FORM_CASE) {
			return context.getString(R.string.token_time_hour_singular);
		}
		else {
			return context.getString(R.string.token_time_hour_plural);
		}
	}
}
