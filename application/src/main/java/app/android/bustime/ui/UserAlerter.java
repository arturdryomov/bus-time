package app.android.bustime.ui;


import android.content.Context;
import android.widget.Toast;


class UserAlerter
{
	public static void alert(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}
}