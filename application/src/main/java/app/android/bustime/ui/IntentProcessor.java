package app.android.bustime.ui;


import android.os.Bundle;
import android.os.Parcelable;


class IntentProcessor
{
	public static boolean haveMessage(Bundle intentExtras) {
		if (intentExtras == null) {
			return false;
		}

		return intentExtras.containsKey(IntentFactory.getMessageId());
	}

	public static Parcelable extractMessage(Bundle intentExtras) {
		return intentExtras.getParcelable(IntentFactory.getMessageId());
	}
}
