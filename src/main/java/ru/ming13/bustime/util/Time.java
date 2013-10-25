package ru.ming13.bustime.util;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Pair;

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
	private static final SimpleDateFormat timeParser;
	private static final PrettyTime relativeTimeFormatter;

	static {
		timeParser = buildTimeParser();
		relativeTimeFormatter = buildRelativeTimeFormatter();
	}

	private static SimpleDateFormat buildTimeParser() {
		return new SimpleDateFormat("HH:mm");
	}

	private static PrettyTime buildRelativeTimeFormatter() {
		PrettyTime formatter = new PrettyTime();

		formatter.removeUnit(Second.class);
		formatter.removeUnit(Millisecond.class);
		formatter.removeUnit(JustNow.class);

		return formatter;
	}

	private final Date date;

	public static Time from(String timeString) {
		return new Time(buildCalendar(timeString));
	}

	private Time(Calendar calendar) {
		this.date = calendar.getTime();
	}

	public static Calendar buildCalendar(String timeString) {
		Calendar calendar = Calendar.getInstance();

		Pair<Integer, Integer> hourMinute = buildHourMinute(buildDate(timeString));
		calendar.set(Calendar.HOUR, hourMinute.first);
		calendar.set(Calendar.MINUTE, hourMinute.second);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar;
	}

	public static Pair<Integer, Integer> buildHourMinute(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);

		return Pair.create(hour, minute);
	}

	public static Date buildDate(String timeString) {
		try {
			return timeParser.parse(timeString);
		} catch (ParseException e) {
			return new Date();
		}
	}

	public String toRelativeString(Context context) {
		Time currentTime = Time.current();

		if (this.equals(currentTime)) {
			return context.getString(R.string.token_time_now);
		}

		return relativeTimeFormatter.setReference(currentTime.toDate()).format(date);
	}

	private static Time current() {
		Calendar calendar = Calendar.getInstance();

		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return new Time(calendar);
	}

	private boolean equals(Time time) {
		Pair<Integer, Integer> otherHourMinute = buildHourMinute(time.toDate());
		Pair<Integer, Integer> thisHourMinute = buildHourMinute(this.toDate());

		return otherHourMinute.equals(thisHourMinute);
	}

	public Date toDate() {
		return date;
	}

	public String toSystemTimeString(Context context) {
		return DateFormat.getTimeFormat(context).format(date);
	}
}
