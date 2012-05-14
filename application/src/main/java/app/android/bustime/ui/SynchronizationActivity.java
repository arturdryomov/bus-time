package app.android.bustime.ui;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import app.android.bustime.R;
import app.android.bustime.local.Synchronizer;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session;


public class SynchronizationActivity extends Activity
{
	private static enum Operation
	{
		NONE, EXPORT, IMPORT, IMPORT_WITH_UPDATING, IMPORT_WITHOUT_UPDATING
	}

	private Operation currentOperation = Operation.NONE;

	private final Context activityContext = this;

	private final static String REMOTE_DATABASE_FILE_NAME = "bustime.db";

	private DropboxAPI<AndroidAuthSession> dropboxApiHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_synchronization);

		initializeBodyControls();

		initializeDropboxSession();
	}

	private void initializeBodyControls() {
		Button importButton = (Button) findViewById(R.id.import_button);
		importButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view) {
				importDatabase();
			}
		});

		Button importWithUpdatingButton = (Button) findViewById(R.id.import_with_updating_button);
		importWithUpdatingButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view) {
				importDatabaseWithUpdating();
			}
		});

		Button importWithoutUpdatingButton = (Button) findViewById(R.id.import_without_updating_button);
		importWithoutUpdatingButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view) {
				importDatabaseWithoutUpdating();
			}
		});

		Button exportButton = (Button) findViewById(R.id.export_button);
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

		callDropboxAuthorization();
	}

	private void importDatabaseWithUpdating() {
		currentOperation = Operation.IMPORT_WITH_UPDATING;

		callDropboxAuthorization();
	}

	private void importDatabaseWithoutUpdating() {
		currentOperation = Operation.IMPORT_WITHOUT_UPDATING;

		callDropboxAuthorization();
	}

	private void exportDatabase() {
		currentOperation = Operation.EXPORT;

		Synchronizer synchronizer = new Synchronizer();
		synchronizer.exportDatabase(getRemoteDatabaseFilePath());

		callDropboxAuthorization();
	}

	private String getRemoteDatabaseFilePath() {
		File filesDirectory = getFilesDir();

		return new File(filesDirectory, REMOTE_DATABASE_FILE_NAME).toString();
	}

	private void callDropboxAuthorization() {
		if (dropboxApiHandler.getSession().isLinked()) {
			finishCurrentTask();
			currentOperation = Operation.NONE;

			return;
		}

		dropboxApiHandler.getSession().startAuthentication(activityContext);
	}

	private void finishCurrentTask() {
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
			case NONE:
				break;
		}
	}

	private void finishImport() {
		downloadFile();

		Synchronizer synchronizer = new Synchronizer();
		synchronizer.importDatabase(getRemoteDatabaseFilePath());

		UserAlerter.alert(activityContext, getString(R.string.message_done));
	}

	private void downloadFile() {
		try {
			FileOutputStream fileStream = new FileOutputStream(getRemoteDatabaseFilePath());
			dropboxApiHandler.getFile(REMOTE_DATABASE_FILE_NAME, null, fileStream, null);
		}
		catch (FileNotFoundException e) {
			// TODO: Notify user
		}
		catch (IOException e) {
			// TODO: Notify user
		}
		catch (DropboxException e) {
			// TODO: Notify user
		}
	}

	private void finishImportWithUpdating() {
		downloadFile();

		Synchronizer synchronizer = new Synchronizer();
		synchronizer.importDatabase(getRemoteDatabaseFilePath(), true);

		UserAlerter.alert(activityContext, getString(R.string.message_done));
	}

	private void finishImportWithoutUpdating() {
		downloadFile();

		Synchronizer synchronizer = new Synchronizer();
		synchronizer.importDatabase(getRemoteDatabaseFilePath(), false);

		UserAlerter.alert(activityContext, getString(R.string.message_done));
	}

	private void finishExport() {
		uploadFile();

		UserAlerter.alert(activityContext, getString(R.string.message_done));
	}

	private void uploadFile() {
		try {
			FileInputStream fileStream = new FileInputStream(getRemoteDatabaseFilePath());
			dropboxApiHandler.putFileOverwrite(REMOTE_DATABASE_FILE_NAME, fileStream,
				fileStream.getChannel().size(), null);
		}
		catch (FileNotFoundException e) {
			// TODO: Notify user
		}
		catch (IOException e) {
			// TODO: Notify user
		}
		catch (DropboxException e) {
			// TODO: Notify user
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (dropboxApiHandler.getSession().authenticationSuccessful()) {
			dropboxApiHandler.getSession().finishAuthentication();

			AccessTokenPair authTokens = dropboxApiHandler.getSession().getAccessTokenPair();

			// TODO: Store auth tokens

			finishCurrentTask();

			currentOperation = Operation.NONE;
		}
	}

	private void initializeDropboxSession() {
		AppKeyPair keys = new AppKeyPair(getString(R.string.dropbox_key),
			getString(R.string.dropbox_secret));
		AndroidAuthSession authSession = new AndroidAuthSession(keys, Session.AccessType.APP_FOLDER);
		dropboxApiHandler = new DropboxAPI<AndroidAuthSession>(authSession);
	}
}
