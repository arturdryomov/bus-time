package ru.ming13.bustime.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Route implements Parcelable
{
	private final long id;
	private final String number;
	private final String description;

	public Route(long id, String number, String description) {
		this.id = id;
		this.number = number;
		this.description = description;
	}

	public long getId() {
		return id;
	}

	public String getNumber() {
		return number;
	}

	public String getDescription() {
		return description;
	}

	public static final Creator<Route> CREATOR = new Creator<Route>()
	{
		@Override
		public Route createFromParcel(Parcel parcel) {
			return new Route(parcel);
		}

		@Override
		public Route[] newArray(int size) {
			return new Route[size];
		}
	};

	private Route(Parcel parcel) {
		this.id = parcel.readLong();
		this.number = parcel.readString();
		this.description = parcel.readString();
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeLong(id);
		parcel.writeString(number);
		parcel.writeString(description);
	}

	@Override
	public int describeContents() {
		return 0;
	}
}
