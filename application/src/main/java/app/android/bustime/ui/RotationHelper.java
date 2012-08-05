package app.android.bustime.ui;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;


final class RotationHelper
{
	private RotationHelper() {
	}

	public static List<RotationSafeTask> buildRetainTasks(RotationSafeTask... tasks) {
		List<RotationSafeTask> retainTasks = new ArrayList<RotationSafeTask>();

		for (RotationSafeTask task : tasks) {
			if (task != null) {
				retainTasks.add(task);
			}
		}

		return retainTasks;
	}

	public static void resetHostActivity(RotationSafeTask... tasks) {
		for (RotationSafeTask task : buildRetainTasks(tasks)) {
			task.resetHostActivity();
		}
	}

	public static <HostActivity extends Activity> void setHostActivity(HostActivity hostActivity, Object retainTasks) {
		if (retainTasks == null) {
			return;
		}

		List<RotationSafeTask<HostActivity>> tasks = (List<RotationSafeTask<HostActivity>>) retainTasks;

		for (RotationSafeTask<HostActivity> task : tasks) {
			task.setHostActivity(hostActivity);
		}
	}
}
