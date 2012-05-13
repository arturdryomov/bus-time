package app.android.bustime.ui;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import app.android.bustime.R;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session;


public class SynchronizationActivity extends Activity
{
	private final Context activityContext = this;

	private DropboxAPI<AndroidAuthSession> dropboxApiHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_synchronization);

		initializeDropboxSession();
	}

	private void initializeDropboxSession() {
		AppKeyPair keys = new AppKeyPair(getString(R.string.dropbox_key),
			getString(R.string.dropbox_secret));
		AndroidAuthSession authSession = new AndroidAuthSession(keys, Session.AccessType.APP_FOLDER);
		dropboxApiHandler = new DropboxAPI<AndroidAuthSession>(authSession);
	}

	private void callDropboxAuthorization() {
		dropboxApiHandler.getSession().startAuthentication(activityContext);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (dropboxApiHandler.getSession().authenticationSuccessful()) {
			dropboxApiHandler.getSession().finishAuthentication();

			AccessTokenPair authTokens = dropboxApiHandler.getSession().getAccessTokenPair();

			// TODO: Store auth tokens
		}
	}
}
