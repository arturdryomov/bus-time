package app.android.bustime.local;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


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
}
