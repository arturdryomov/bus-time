package ru.ming13.bustime.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import ru.ming13.bustime.R;

public final class Time
{
	private final Date date;

	@NonNull
	public static Time from(@Nullable String databaseTimeString) {
		return new Time(createCalendar(createDate(databaseTimeString)));
	}

	private Time(Calendar calendar) {
		this.date = calendar.getTime();
	}

	private static Calendar createCalendar(Date date) {
		Calendar calendar = Calendar.getInstance();

		calendar.setTime(date);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar;
	}

	private static Date createDate(String databaseTimeString) {
		if (Strings.isBlank(databaseTimeString)) {
			return new Date(0);
		}

		try {
			return Formatters.getDatabaseTimeFormatter().parse(databaseTimeString);
		} catch (ParseException e) {
			return new Date(0);
		}
	}

	@NonNull
	public static Time current() {
		Calendar calendar = Calendar.getInstance();

		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return new Time(calendar);
	}

	public boolean isAfter(@NonNull Time time) {
		return this.date.after(time.date);
	}

	public boolean isEmpty() {
		return date.getTime() == 0;
	}

	public boolean isWeekend() {
		int dayOfWeek = createCalendar(date).get(Calendar.DAY_OF_WEEK);

		return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY;
	}

	@NonNull
	public String toRelativeString(@NonNull Context context) {
		Time currentTime = Time.current();

		if (this.date.equals(currentTime.date)) {
			return context.getString(R.string.token_time_now);
		}

		return Formatters.getRelativeTimeFormatter().setReference(currentTime.date).format(date);
	}

	@NonNull
	public String toSystemString(@NonNull Context context) {
		return Formatters.getSystemTimeFormatter(context).format(date);
	}
}
