package app.android.bustime.db;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.content.Context;
import android.text.format.DateUtils;


public class Time
{
	private static final DateFormat timeFormatter;

	static {
		timeFormatter = new SimpleDateFormat("HH:mm");
	}

	private final int hours;
	private final int minutes;

	public static Time parse(String timeStringRepresentation) {
		return new Time(timeStringRepresentation);
	}

	private Time(String timeStringRepresentation) {
		Calendar calendar = parseTime(timeStringRepresentation);

		hours = calendar.get(Calendar.HOUR_OF_DAY);
		minutes = calendar.get(Calendar.MINUTE);
	}

	private Calendar parseTime(String timeStringRepresentation) {
		try {
			Date date = timeFormatter.parse(timeStringRepresentation);

			return convertDateToCalendar(date);
		}
		catch (ParseException e) {
			throw new TimeException();
		}
	}

	private Calendar convertDateToCalendar(Date date) {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(date);

		return calendar;
	}

	private Time(Calendar calendar) {
		hours = calendar.get(Calendar.HOUR_OF_DAY);
		minutes = calendar.get(Calendar.MINUTE);
	}

	public static Time getInstance() {
		Calendar calendar = GregorianCalendar.getInstance();

		return new Time(calendar);
	}

	public Time sum(Time timeToSum) {
		Calendar calendar = getCalendar();
		calendar.add(Calendar.HOUR_OF_DAY, timeToSum.hours);
		calendar.add(Calendar.MINUTE, timeToSum.minutes);

		return new Time(calendar);
	}

	private Calendar getCalendar() {
		return convertTimeToCalendar(this);
	}

	private Calendar convertTimeToCalendar(Time time) {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, time.hours);
		calendar.set(Calendar.MINUTE, time.minutes);

		return calendar;
	}

	public boolean isNow() {
		Time now = Time.getInstance();

		return now.hours == hours && now.minutes == minutes;
	}

	public boolean isAfter(Time timeToCompare) {
		return getCalendar().after(convertTimeToCalendar(timeToCompare));
	}

	public String toSystemFormattedString(Context context) {
		DateFormat systemTimeFormat = android.text.format.DateFormat.getTimeFormat(context);

		return systemTimeFormat.format(getDate());
	}

	private Date getDate() {
		return getCalendar().getTime();
	}

	public String toRelativeToNowSpanString() {
		long timeInMilliseconds = getCalendar().getTimeInMillis();
		long nowInMilliseconds = Time.getInstance().getCalendar().getTimeInMillis();

		// TODO: Check adding flags as forth parameter
		return DateUtils.getRelativeTimeSpanString(timeInMilliseconds, nowInMilliseconds,
			DateUtils.MINUTE_IN_MILLIS).toString();
	}

	public String toDatabaseString() {
		return timeFormatter.format(getDate());
	}
}
