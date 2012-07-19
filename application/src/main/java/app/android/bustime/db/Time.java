package app.android.bustime.db;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.content.Context;
import android.text.format.DateFormat;
import android.text.format.DateUtils;


public class Time
{
	private static final int MAXIMUM_HOURS_COUNT = 23;
	private static final int MINIMUM_HOURS_COUNT = 0;
	private static final int MAXIMUM_MINUTES_COUNT = 59;
	private static final int MINIMUM_MINUTES_COUNT = 0;

	private final int hours;
	private final int minutes;

	private static final SimpleDateFormat timeFormatter;

	static {
		timeFormatter = new SimpleDateFormat("HH:mm");
	}

	public Time(String timeAsString) {
		Date time = getTime(timeAsString);

		int hours = time.getHours();
		int minutes = time.getMinutes();

		if (!isTimeCorrect(hours, minutes)) {
			throw new TimeException();
		}

		this.hours = hours;
		this.minutes = minutes;
	}

	private Date getTime(String timeAsString) {
		try {
			Date time = timeFormatter.parse(timeAsString);

			if (time == null) {
				throw new TimeException();
			}

			return time;
		}
		catch (ParseException e) {
			throw new TimeException();
		}
	}

	private boolean isTimeCorrect(int hours, int minutes) {
		if ((hours > MAXIMUM_HOURS_COUNT) || (hours < MINIMUM_HOURS_COUNT)) {
			return false;
		}

		if ((minutes > MAXIMUM_MINUTES_COUNT) || (minutes < MINIMUM_MINUTES_COUNT)) {
			return false;
		}

		return true;
	}

	public Time(int hours, int minutes) {
		if (!isTimeCorrect(hours, minutes)) {
			throw new TimeException();
		}

		this.hours = hours;
		this.minutes = minutes;
	}

	@Override
	public String toString() {
		return timeFormatter.format(getTime());
	}

	private Date getTime() {
		Calendar calendar = new GregorianCalendar();
		calendar.set(Calendar.HOUR_OF_DAY, hours);
		calendar.set(Calendar.MINUTE, minutes);

		return calendar.getTime();
	}

	public String toString(Context activityContext) {
		return DateFormat.getTimeFormat(activityContext).format(getTime());
	}

	public long getMilliseconds() {
		return hours * DateUtils.HOUR_IN_MILLIS + minutes * DateUtils.MINUTE_IN_MILLIS;
	}

	public Time sum(Time timeToSum) {
		Calendar calendar = new GregorianCalendar();
		calendar.set(Calendar.HOUR_OF_DAY, hours);
		calendar.set(Calendar.MINUTE, minutes);

		calendar.add(Calendar.HOUR_OF_DAY, timeToSum.hours);
		calendar.add(Calendar.MINUTE, timeToSum.minutes);

		int resultHours = calendar.get(Calendar.HOUR_OF_DAY);
		int resultMinutes = calendar.get(Calendar.MINUTE);

		return new Time(resultHours, resultMinutes);
	}

	public boolean isAfter(Time time) {
		return getTime().after(time.getTime());
	}

	public static Time getCurrentTime() {
		final Calendar calendar = GregorianCalendar.getInstance();

		return new Time(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
	}

	public boolean isNow() {
		Time now = getCurrentTime();

		return now.hours == hours && now.minutes == minutes;
	}
}
