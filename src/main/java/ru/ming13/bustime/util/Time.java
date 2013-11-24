package ru.ming13.bustime.util;

import android.content.Context;
import android.text.format.DateFormat;

import org.ocpsoft.prettytime.PrettyTime;
import org.ocpsoft.prettytime.units.JustNow;
import org.ocpsoft.prettytime.units.Millisecond;
import org.ocpsoft.prettytime.units.Second;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ru.ming13.bustime.R;

public final class Time
{
	private static final SimpleDateFormat databaseTimeFormatter;
	private static final PrettyTime relativeTimeFormatter;

	static {
		databaseTimeFormatter = buildDatabaseTimeFormatter();
		relativeTimeFormatter = buildRelativeTimeFormatter();
	}

	private static SimpleDateFormat buildDatabaseTimeFormatter() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm");
	}

	private static PrettyTime buildRelativeTimeFormatter() {
		PrettyTime formatter = new PrettyTime();

		formatter.removeUnit(Second.class);
		formatter.removeUnit(Millisecond.class);
		formatter.removeUnit(JustNow.class);

		return formatter;
	}

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
		try {
			return databaseTimeFormatter.parse(databaseTimeString);
		} catch (ParseException e) {
			return new Date();
		}
	}

	public static Time current() {
		Calendar calendar = Calendar.getInstance();

		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return new Time(calendar);
	}

	public boolean isWeekend() {
		int dayOfWeek = buildCalendar(date).get(Calendar.DAY_OF_WEEK);

		return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY;
	}

	public String toDatabaseString() {
		return databaseTimeFormatter.format(date);
	}

	public String toRelativeString(Context context) {
		Time currentTime = Time.current();

		if (this.date.equals(currentTime.date)) {
			return context.getString(R.string.token_time_now);
		}

		return relativeTimeFormatter.setReference(currentTime.date).format(date);
	}

	public String toSystemString(Context context) {
		return DateFormat.getTimeFormat(context).format(date);
	}
}
