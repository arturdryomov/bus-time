package ru.ming13.bustime.util;

import android.content.Context;
import android.support.annotation.NonNull;

import org.ocpsoft.prettytime.PrettyTime;
import org.ocpsoft.prettytime.units.JustNow;
import org.ocpsoft.prettytime.units.Millisecond;
import org.ocpsoft.prettytime.units.Second;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public final class Formatters
{
	private Formatters() {
	}

	private static DateFormat databaseTimeFormatter;
	private static PrettyTime relativeTimeFormatter;
	private static DateFormat systemTimeFormatter;

	@NonNull
	public static DateFormat getDatabaseTimeFormatter() {
		if (databaseTimeFormatter == null) {
			databaseTimeFormatter = createDatabaseTimeFormatter();
		}

		return databaseTimeFormatter;
	}

	private static DateFormat createDatabaseTimeFormatter() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
	}

	@NonNull
	public static PrettyTime getRelativeTimeFormatter() {
		if (relativeTimeFormatter == null) {
			relativeTimeFormatter = createRelativeTimeFormatter();
		}

		return relativeTimeFormatter;
	}

	@NonNull
	private static PrettyTime createRelativeTimeFormatter() {
		PrettyTime formatter = new PrettyTime();

		formatter.removeUnit(Second.class);
		formatter.removeUnit(Millisecond.class);
		formatter.removeUnit(JustNow.class);

		return formatter;
	}

	@NonNull
	public static DateFormat getSystemTimeFormatter(@NonNull Context context) {
		if (systemTimeFormatter == null) {
			systemTimeFormatter = createSystemTimeFormatter(context);
		}

		return systemTimeFormatter;
	}

	private static DateFormat createSystemTimeFormatter(Context context) {
		return android.text.format.DateFormat.getTimeFormat(context);
	}

	public static void tearDownFormatters() {
		databaseTimeFormatter = null;
		relativeTimeFormatter = null;
		systemTimeFormatter = null;
	}
}
