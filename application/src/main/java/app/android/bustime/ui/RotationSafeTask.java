package app.android.bustime.ui;


import android.app.Activity;
import android.os.AsyncTask;


abstract class RotationSafeTask<HostActivity extends Activity> extends AsyncTask<Void, Void, String>
{
	private HostActivity hostActivity;

	public RotationSafeTask() {
		super();

		this.hostActivity = null;
	}

	public void setHostActivity(HostActivity hostActivity) {
		this.hostActivity = hostActivity;

		onSettingHostActivity();
	}

	protected abstract void onSettingHostActivity();

	protected HostActivity getHostActivity() {
		return hostActivity;
	}

	public void resetHostActivity() {
		hostActivity = null;

		onResettingHostActivity();
	}

	protected abstract void onResettingHostActivity();

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		if (isHostActivityAvailable()) {
			onBeforeExecution();
		}
	}

	private boolean isHostActivityAvailable() {
		return hostActivity != null;
	}

	protected abstract void onBeforeExecution();

	@Override
	protected void onPostExecute(String errorMessage) {
		super.onPostExecute(errorMessage);

		if (isHostActivityAvailable()) {
			onAfterExecution(errorMessage);
		}
	}

	protected abstract void onAfterExecution(String errorMessage);
}
