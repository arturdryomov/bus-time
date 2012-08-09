package app.android.bustime.ui.util;


import android.content.Context;
import android.widget.Toast;


public class UserAlerter
{
	public static void alert(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}
}