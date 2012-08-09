package app.android.bustime.ui;


import android.os.Bundle;
import android.os.Parcelable;


public final class IntentProcessor
{
	private IntentProcessor() {
	}

	public static boolean haveMessage(Bundle intentExtras) {
		if (intentExtras == null) {
			return false;
		}

		return intentExtras.containsKey(IntentFactory.getMessageId());
	}

	public static Parcelable extractMessage(Bundle intentExtras) {
		return intentExtras.getParcelable(IntentFactory.getMessageId());
	}

	public static boolean haveExtraMessage(Bundle intentExtras) {
		if (intentExtras == null) {
			return false;
		}

		return intentExtras.containsKey(IntentFactory.getExtraMessageId());
	}

	public static Parcelable extractExtraMessage(Bundle intentExtras) {
		return intentExtras.getParcelable(IntentFactory.getExtraMessageId());
	}
}
