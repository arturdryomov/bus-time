package app.android.bustime.ui;


import android.os.Bundle;
import android.os.Parcelable;


final class FragmentProcessor
{
	private FragmentProcessor() {
	}

	public static boolean haveMessage(Bundle arguments) {
		if (arguments == null) {
			return false;
		}

		return arguments.containsKey(FragmentFactory.getMessageId());
	}

	public static Parcelable extractMessage(Bundle arguments) {
		return arguments.getParcelable(FragmentFactory.getMessageId());
	}

	public static boolean haveExtraMessage(Bundle arguments) {
		if (arguments == null) {
			return false;
		}

		return arguments.containsKey(FragmentFactory.getExtraMessageId());
	}

	public static Parcelable extractExtraMessage(Bundle arguments) {
		return arguments.getParcelable(FragmentFactory.getExtraMessageId());
	}
}
