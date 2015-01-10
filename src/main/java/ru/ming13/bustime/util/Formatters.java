package ru.ming13.bustime.util;

import android.content.Context;
import android.support.annotation.NonNull;

import org.ocpsoft.prettytime.PrettyTime;
import org.ocpsoft.prettytime.units.JustNow;
import org.ocpsoft.prettytime.units.Millisecond;
import org.ocpsoft.prettytime.units.Second;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public final class Formatters
{
	private Formatters() {
	}

	private static DateFormat databaseTimeFormatter;
	private static PrettyTime relativeTimeFormatter;
	private static DateFormat systemTimeFormatter;

	public static DateFormat getDatabaseTimeFormatter() {
		if (databaseTimeFormatter == null) {
			databaseTimeFormatter = buildDatabaseTimeFormatter();
		}

		return databaseTimeFormatter;
	}

	private static DateFormat buildDatabaseTimeFormatter() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm");
	}

	public static PrettyTime getRelativeTimeFormatter() {
		if (relativeTimeFormatter == null) {
			relativeTimeFormatter = buildRelativeTimeFormatter();
		}

		return relativeTimeFormatter;
	}

	private static PrettyTime buildRelativeTimeFormatter() {
		PrettyTime formatter = new PrettyTime();

		formatter.removeUnit(Second.class);
		formatter.removeUnit(Millisecond.class);
		formatter.removeUnit(JustNow.class);

		return formatter;
	}

	public static DateFormat getSystemTimeFormatter(@NonNull Context context) {
		if (systemTimeFormatter == null) {
			systemTimeFormatter = buildSystemTimeFormatter(context);
		}

		return systemTimeFormatter;
	}

	private static DateFormat buildSystemTimeFormatter(Context context) {
		return android.text.format.DateFormat.getTimeFormat(context);
	}

	public static void tearDownFormatters() {
		databaseTimeFormatter = null;
		relativeTimeFormatter = null;
		systemTimeFormatter = null;
	}
}
