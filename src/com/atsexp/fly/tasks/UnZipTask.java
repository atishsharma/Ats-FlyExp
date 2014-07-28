package com.atsexp.fly.tasks;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;

import com.atsexp.fly.Browser;
import com.atsexp.fly.utils.SimpleUtils;
import com.atsexp.fly.utils.ZipUtils;
import com.atsexp.fly.R;

public final class UnZipTask extends AsyncTask<String, Void, List<String>> {

	private final WeakReference<Activity> activity;

	private ProgressDialog dialog;

	public UnZipTask(final Activity activity) {
		this.activity = new WeakReference<Activity>(activity);
	}

	@Override
	protected void onPreExecute() {
		final Activity activity = this.activity.get();

		if (activity != null) {
			this.dialog = new ProgressDialog(activity);
			this.dialog.setMessage(activity.getString(R.string.unzipping));
			this.dialog.setCancelable(true);
			this.dialog
					.setOnCancelListener(new DialogInterface.OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
							cancel(false);
						}
					});
			if (!activity.isFinishing()) {
				this.dialog.show();
			}
		}
	}

	@Override
	protected List<String> doInBackground(String... files) {
		final Activity activity = this.activity.get();
		final List<String> failed = new ArrayList<String>();

		try {
			ZipUtils.unpackZip(files[0], files[1]);
		} catch (Exception e) {
			failed.add(files.toString());
		}

		SimpleUtils.requestMediaScanner(activity,
				new File(files[1]).listFiles());
		return failed;
	}

	@Override
	protected void onPostExecute(final List<String> failed) {
		super.onPostExecute(failed);
		this.finish(failed);
	}

	@Override
	protected void onCancelled(final List<String> failed) {
		super.onCancelled(failed);
		this.finish(failed);
	}

	private void finish(final List<String> failed) {
		if (this.dialog != null) {
			this.dialog.dismiss();
		}

		Browser.listDirectory(Browser.mCurrentPath);

		final Activity activity = this.activity.get();
		if (activity != null && !failed.isEmpty()) {
			Toast.makeText(activity, activity.getString(R.string.cantopenfile),
					Toast.LENGTH_SHORT).show();
			if (!activity.isFinishing()) {
				dialog.show();
			}
		}
	}
}
