package ru.ming13.bustime.ui.util;


import android.content.Context;
import android.widget.Toast;


public class UserAlerter
{
	public static void alert(Context context, int textResourceId) {
		Toast.makeText(context, context.getText(textResourceId), Toast.LENGTH_SHORT).show();
	}
}