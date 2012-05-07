package app.android.bustime.local;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.os.Parcel;
import android.os.Parcelable;


public class Time implements Parcelable
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

	public int getHours() {
		return hours;
	}

	public int getMinutes() {
		return minutes;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeInt(hours);
		parcel.writeInt(minutes);
	}

	public static final Parcelable.Creator<Time> CREATOR = new Parcelable.Creator<Time>() {
		@Override
		public Time createFromParcel(Parcel parcel) {
			return new Time(parcel);
		};

		@Override
		public Time[] newArray(int size) {
			return new Time[size];
		};
	};

	private Time(Parcel parcel) {
		hours = parcel.readInt();
		minutes = parcel.readInt();
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

	public Time difference(Time timeToDiffer) {
		Calendar calendar = new GregorianCalendar();
		calendar.set(Calendar.HOUR_OF_DAY, hours);
		calendar.set(Calendar.MINUTE, minutes);

		calendar.add(Calendar.HOUR_OF_DAY, -timeToDiffer.hours);
		calendar.add(Calendar.MINUTE, -timeToDiffer.minutes);

		int resultHours = calendar.get(Calendar.HOUR_OF_DAY);
		int resultMinutes = calendar.get(Calendar.MINUTE);

		return new Time(resultHours, resultMinutes);
	}

	public boolean isAfter(Time time) {
		return getTime().after(time.getTime());
	}

	public static Time getCurrentTime() {
		final Calendar calendar = Calendar.getInstance();

		return new Time(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
	}

	@Override
	public boolean equals(Object otherObject) {
		Time otherTime = (Time) otherObject;

		return (otherTime.hours == this.hours) && (otherTime.minutes == this.minutes);
	}
}
