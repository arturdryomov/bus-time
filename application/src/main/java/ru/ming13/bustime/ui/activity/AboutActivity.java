package ru.ming13.bustime.ui.activity;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockActivity;
import org.apache.commons.lang3.StringUtils;
import ru.ming13.bustime.R;
import ru.ming13.bustime.ui.intent.IntentFactory;


public class AboutActivity extends SherlockActivity
{
	private static final int PACKAGE_INFO_FLAGS = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		setUpApplicationVersion();

		setUpRatingApplication();
		setUpSendingFeedback();
	}

	private void setUpApplicationVersion() {
		TextView versionText = (TextView) findViewById(R.id.text_version);

		versionText.setText(buildVersionText());
	}

	private String buildVersionText() {
		return String.format("%s %s", getString(R.string.token_version), getApplicationVersion());
	}

	private String getApplicationVersion() {
		try {
			return getPackageManager().getPackageInfo(getPackageName(), PACKAGE_INFO_FLAGS).versionName;
		}
		catch (PackageManager.NameNotFoundException e) {
			return StringUtils.EMPTY;
		}
	}

	private void setUpRatingApplication() {
		Button rateApplicationButton = (Button) findViewById(R.id.button_rate_application);

		rateApplicationButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view) {
				callGooglePlay();
			}
		});
	}

	private void callGooglePlay() {
		try {
			Intent intent = IntentFactory.createGooglePlayIntent(buildAppGooglePlayUrl());
			startActivity(intent);
		}
		catch (ActivityNotFoundException e) {
			Intent intent = IntentFactory.createGooglePlayIntent(buildWebGooglePlayUrl());
			startActivity(intent);
		}
	}

	private String buildAppGooglePlayUrl() {
		return String.format(getString(R.string.url_app_google_play), getPackageName());
	}

	private String buildWebGooglePlayUrl() {
		return String.format(getString(R.string.url_web_google_play), getPackageName());
	}

	private void setUpSendingFeedback() {
		Button sendFeedbackButton = (Button) findViewById(R.id.button_send_feedback);

		sendFeedbackButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view) {
				callSendingFeedback();
			}
		});
	}

	private void callSendingFeedback() {
		Intent intent = IntentFactory.createEmailIntent(getString(R.string.email_address_feedback),
			getString(R.string.email_subject_feedback));
		startActivity(Intent.createChooser(intent, null));
	}
}
