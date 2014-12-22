package ru.ming13.bustime.util;

import android.content.Context;
import android.text.format.DateFormat;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import ru.ming13.bustime.R;

public final class Time
{
	private final Date date;

	public static Time from(String databaseTimeString) {
		return new Time(buildCalendar(buildDate(databaseTimeString)));
	}

	private Time(Calendar calendar) {
		this.date = calendar.getTime();
	}

	private static Calendar buildCalendar(Date date) {
		Calendar calendar = Calendar.getInstance();

		calendar.setTime(date);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar;
	}

	private static Date buildDate(String databaseTimeString) {
		if (StringUtils.isBlank(databaseTimeString)) {
			return new Date(0);
		}

		try {
			return Formatters.getDatabaseTimeFormatter().parse(databaseTimeString);
		} catch (ParseException e) {
			return new Date(0);
		}
	}

	public static Time current() {
		Calendar calendar = Calendar.getInstance();

		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return new Time(calendar);
	}

	public boolean isEmpty() {
		return date.getTime() == 0;
	}

	public boolean isWeekend() {
		int dayOfWeek = buildCalendar(date).get(Calendar.DAY_OF_WEEK);

		return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY;
	}

	public String toDatabaseString() {
		return Formatters.getDatabaseTimeFormatter().format(date);
	}

	public String toRelativeString(Context context) {
		Time currentTime = Time.current();

		if (this.date.equals(currentTime.date)) {
			return context.getString(R.string.token_time_now);
		}

		return Formatters.getRelativeTimeFormatter().setReference(currentTime.date).format(date);
	}

	public String toSystemString(Context context) {
		return DateFormat.getTimeFormat(context).format(date);
	}
}
