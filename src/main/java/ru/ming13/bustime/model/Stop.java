package ru.ming13.bustime.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Stop implements Parcelable
{
	private final long id;
	private final String name;
	private final String direction;

	public Stop(long id, String name, String direction) {
		this.id = id;
		this.name = name;
		this.direction = direction;
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
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeLong(id);
		parcel.writeString(name);
		parcel.writeString(direction);
	}

	@Override
	public int describeContents() {
		return 0;
	}
}
