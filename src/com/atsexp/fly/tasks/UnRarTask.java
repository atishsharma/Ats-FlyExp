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
import com.atsexp.fly.R;
import com.github.junrar.extract.ExtractArchive;

public final class UnRarTask extends AsyncTask<String, Void, List<String>> {

	private final WeakReference<Activity> activity;

	private ProgressDialog dialog;

	public UnRarTask(final Activity activity) {
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
	protected List<String> doInBackground(String... file) {
		final Activity activity = this.activity.get();
		final List<String> failed = new ArrayList<String>();
		final File rar = new File(file[0]);
		final File destinationFolder = new File(file[1]);

		if (!destinationFolder.exists())
			destinationFolder.mkdirs();

		try {
			ExtractArchive extractArchive = new ExtractArchive();
			extractArchive.extractArchive(rar, destinationFolder);
		} catch (Exception e) {
			failed.add(file[0]);
		}

		SimpleUtils
				.requestMediaScanner(activity, destinationFolder.listFiles());
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
		}
	}
}
