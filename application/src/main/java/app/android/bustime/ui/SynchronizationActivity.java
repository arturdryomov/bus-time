package app.android.bustime.ui;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import app.android.bustime.R;
import app.android.bustime.db.SyncException;
import app.android.bustime.db.Synchronizer;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session;


public class SynchronizationActivity extends Activity
{
	private final Context activityContext = this;

	private static enum Operation
	{
		NONE, EXPORT, IMPORT, IMPORT_WITH_UPDATING, IMPORT_WITHOUT_UPDATING
	}

	private Operation currentOperation = Operation.NONE;

	private final static String REMOTE_DATABASE_FILE_NAME = "bustime.db";
	private DropboxAPI<AndroidAuthSession> dropboxApiHandler;

	private final static String PREFERENCE_AUTH_KEY = "auth_key";
	private final static String PREFERENCE_AUTH_SECRET = "auth_secret";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_synchronization);

		initializeBodyControls();

		initializeDropboxSession();
	}

	private void initializeBodyControls() {
		Button importButton = (Button) findViewById(R.id.button_import);
		importButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view) {
				importDatabase();
			}
		});

		Button importWithUpdatingButton = (Button) findViewById(R.id.button_import_with_updating);
		importWithUpdatingButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view) {
				importDatabaseWithUpdating();
			}
		});

		Button importWithoutUpdatingButton = (Button) findViewById(R.id.button_import_without_updating);
		importWithoutUpdatingButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view) {
				importDatabaseWithoutUpdating();
			}
		});

		Button exportButton = (Button) findViewById(R.id.button_export);
		exportButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view) {
				exportDatabase();
			}
		});
	}

	private void importDatabase() {
		currentOperation = Operation.IMPORT;

		continueCurrentOperation();
	}

	private void importDatabaseWithUpdating() {
		currentOperation = Operation.IMPORT_WITH_UPDATING;

		continueCurrentOperation();
	}

	private void importDatabaseWithoutUpdating() {
		currentOperation = Operation.IMPORT_WITHOUT_UPDATING;

		continueCurrentOperation();
	}

	private void exportDatabase() {
		currentOperation = Operation.EXPORT;

		continueCurrentOperation();
	}

	private void continueCurrentOperation() {
		if (dropboxApiHandler.getSession().isLinked()) {
			finishCurrentOperation();

			return;
		}

		if (areAuthKeysStored()) {
			dropboxApiHandler.getSession().setAccessTokenPair(loadAuthTokens());
			finishCurrentOperation();

			return;
		}

		startDropboxAuthentication();
	}

	private boolean areAuthKeysStored() {
		SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);

		if (sharedPreferences.getString(PREFERENCE_AUTH_KEY, null) == null) {
			return false;
		}
		if (sharedPreferences.getString(PREFERENCE_AUTH_SECRET, null) == null) {
			return false;
		}

		return true;
	}

	private AccessTokenPair loadAuthTokens() {
		SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);

		String authTokenKey = sharedPreferences.getString(PREFERENCE_AUTH_KEY, new String());
		String authTokenSecret = sharedPreferences.getString(PREFERENCE_AUTH_SECRET, new String());

		return new AccessTokenPair(authTokenKey, authTokenSecret);
	}

	private void startDropboxAuthentication() {
		dropboxApiHandler.getSession().startAuthentication(activityContext);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (currentOperation == Operation.NONE) {
			return;
		}

		if (dropboxApiHandler.getSession().authenticationSuccessful()) {
			dropboxApiHandler.getSession().finishAuthentication();

			storeAuthTokens(dropboxApiHandler.getSession().getAccessTokenPair());

			finishCurrentOperation();
		}
	}

	private void storeAuthTokens(AccessTokenPair authTokens) {
		SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
		SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();

		preferencesEditor.putString(PREFERENCE_AUTH_KEY, authTokens.key);
		preferencesEditor.putString(PREFERENCE_AUTH_SECRET, authTokens.secret);

		preferencesEditor.commit();
	}

	private void finishCurrentOperation() {
		new FinishCurrentOperationTask().execute();
	}

	private class FinishCurrentOperationTask extends AsyncTask<Void, Void, String>
	{
		private boolean isUnlinked = false;

		private ProgressDialogShowHelper progressDialogHelper;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			progressDialogHelper = new ProgressDialogShowHelper();
			progressDialogHelper.show(activityContext, getString(R.string.loading_sync));
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				switch (currentOperation) {
					case IMPORT:
						finishImport();
						break;
					case IMPORT_WITH_UPDATING:
						finishImportWithUpdating();
						break;
					case IMPORT_WITHOUT_UPDATING:
						finishImportWithoutUpdating();
						break;
					case EXPORT:
						finishExport();
						break;
				}
			}
			catch (DropboxAuthException e) {
				isUnlinked = true;
			}
			catch (DropboxRemoteFileNotFoundException e) {
				return getString(R.string.error_remote_database_does_not_exist);
			}
			catch (SyncException e) {
				return getString(R.string.error_unspecified);
			}
			finally {
				removeRemoteDatabaseFile();
			}

			return new String();
		}

		@Override
		protected void onPostExecute(String errorMessage) {
			super.onPostExecute(errorMessage);

			progressDialogHelper.hide();

			if (isUnlinked) {
				startDropboxAuthentication();

				return;
			}

			if (!errorMessage.isEmpty()) {
				UserAlerter.alert(activityContext, errorMessage);
			}

			currentOperation = Operation.NONE;
		}
	}

	private void finishImport() {
		downloadRemoteDatabase();

		Synchronizer synchronizer = new Synchronizer();
		synchronizer.importDatabase(getRemoteDatabaseFilePath());
	}

	private void downloadRemoteDatabase() {
		try {
			FileOutputStream remoteDatabaseFileStream = new FileOutputStream(getRemoteDatabaseFilePath());
			dropboxApiHandler.getFile(REMOTE_DATABASE_FILE_NAME, null, remoteDatabaseFileStream, null);
		}
		catch (FileNotFoundException e) {
			throw new SyncException();
		}
		catch (IOException e) {
			throw new SyncException();
		}
		catch (DropboxUnlinkedException e) {
			throw new DropboxAuthException();
		}
		catch (DropboxServerException e) {
			if (e.error == DropboxServerException._404_NOT_FOUND) {
				throw new DropboxRemoteFileNotFoundException();
			}
		}
		catch (DropboxException e) {
			throw new SyncException();
		}
	}

	private String getRemoteDatabaseFilePath() {
		File filesDirectory = getFilesDir();

		return new File(filesDirectory, REMOTE_DATABASE_FILE_NAME).toString();
	}

	private void finishImportWithUpdating() {
		downloadRemoteDatabase();

		Synchronizer synchronizer = new Synchronizer();
		synchronizer.importDatabase(getRemoteDatabaseFilePath(), true);
	}

	private void finishImportWithoutUpdating() {
		downloadRemoteDatabase();

		Synchronizer synchronizer = new Synchronizer();
		synchronizer.importDatabase(getRemoteDatabaseFilePath(), false);
	}

	private void finishExport() {
		Synchronizer synchronizer = new Synchronizer();
		synchronizer.exportDatabase(getRemoteDatabaseFilePath());

		uploadLocalDatabase();
	}

	private void uploadLocalDatabase() {
		try {
			FileInputStream localDatabaseFileStream = new FileInputStream(getRemoteDatabaseFilePath());
			dropboxApiHandler.putFileOverwrite(REMOTE_DATABASE_FILE_NAME, localDatabaseFileStream,
				localDatabaseFileStream.getChannel().size(), null);
		}
		catch (FileNotFoundException e) {
			throw new SyncException();
		}
		catch (IOException e) {
			throw new SyncException();
		}
		catch (DropboxUnlinkedException e) {
			throw new DropboxAuthException();
		}
		catch (DropboxException e) {
			throw new SyncException();
		}
	}

	private void removeRemoteDatabaseFile() {
		activityContext.deleteFile(REMOTE_DATABASE_FILE_NAME);
	}

	private void initializeDropboxSession() {
		AppKeyPair keys = new AppKeyPair(getString(R.string.dropbox_key),
			getString(R.string.dropbox_secret));
		AndroidAuthSession authSession = new AndroidAuthSession(keys, Session.AccessType.APP_FOLDER);
		dropboxApiHandler = new DropboxAPI<AndroidAuthSession>(authSession);
	}
}
