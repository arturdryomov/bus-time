package ru.ming13.bustime.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Stop implements Parcelable
{
	private final long id;
	private final String name;
	private final String direction;
	private final double latitude;
	private final double longitude;

	public Stop(long id, String name, String direction) {
		this.id = id;
		this.name = name;
		this.direction = direction;
		this.latitude = 0;
		this.longitude = 0;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDirection() {
		return direction;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public static final Creator<Stop> CREATOR = new Creator<Stop>()
	{
		@Override
		public Stop createFromParcel(Parcel parcel) {
			return new Stop(parcel);
		}

		@Override
		public Stop[] newArray(int size) {
			return new Stop[size];
		}
	};

	private Stop(Parcel parcel) {
		this.id = parcel.readLong();
		this.name = parcel.readString();
		this.direction = parcel.readString();
		this.latitude = parcel.readDouble();
		this.longitude = parcel.readDouble();
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeLong(id);
		parcel.writeString(name);
		parcel.writeString(direction);
		parcel.writeDouble(latitude);
		parcel.writeDouble(longitude);
	}

	@Override
	public int describeContents() {
		return 0;
	}
}
