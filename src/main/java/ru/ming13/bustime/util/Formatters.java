package ru.ming13.bustime.util;

import org.ocpsoft.prettytime.PrettyTime;
import org.ocpsoft.prettytime.units.JustNow;
import org.ocpsoft.prettytime.units.Millisecond;
import org.ocpsoft.prettytime.units.Second;

import java.text.SimpleDateFormat;

public final class Formatters
{
	private Formatters() {
	}

	private static SimpleDateFormat databaseTimeFormatter;
	private static PrettyTime relativeTimeFormatter;

	public static SimpleDateFormat getDatabaseTimeFormatter() {
		if (databaseTimeFormatter == null) {
			databaseTimeFormatter = buildDatabaseTimeFormatter();
		}

		return databaseTimeFormatter;
	}

	private static SimpleDateFormat buildDatabaseTimeFormatter() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm");
	}

	public static PrettyTime getRelativeTimeFormatter() {
		if (relativeTimeFormatter == null) {
			relativeTimeFormatter = buildRelativeTimeFormatter();
		}

		return buildRelativeTimeFormatter();
	}

	private static PrettyTime buildRelativeTimeFormatter() {
		PrettyTime formatter = new PrettyTime();

		formatter.removeUnit(Second.class);
		formatter.removeUnit(Millisecond.class);
		formatter.removeUnit(JustNow.class);

		return formatter;
	}

	public static void tearDownFormatters() {
		databaseTimeFormatter = null;
		relativeTimeFormatter = null;
	}
}
