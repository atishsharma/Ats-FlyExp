package com.atsexp.fly.tasks;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;

import com.atsexp.fly.utils.ZipUtils;
import com.atsexp.fly.R;

public final class ZipFolderTask extends
		AsyncTask<String, Void, ArrayList<String>> {

	private final WeakReference<Activity> activity;

	private ProgressDialog dialog;

	private String zipname;

	public ZipFolderTask(final Activity activity, String newpath) {
		this.activity = new WeakReference<Activity>(activity);
		this.zipname = newpath;
	}

	@Override
	protected void onPreExecute() {
		final Activity activity = this.activity.get();

		if (activity != null) {
			this.dialog = new ProgressDialog(activity);
			this.dialog.setMessage(activity.getString(R.string.zipping));
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
	protected ArrayList<String> doInBackground(String... files) {
		final ArrayList<String> failed = new ArrayList<String>();

		try {
			ZipUtils.createZipFile(files[0], zipname);
		} catch (Exception e) {
			failed.add(files.toString());
		}
		return failed;
	}

	@Override
	protected void onPostExecute(final ArrayList<String> failed) {
		super.onPostExecute(failed);
		this.finish(failed);
	}

	@Override
	protected void onCancelled(final ArrayList<String> failed) {
		super.onCancelled(failed);
		this.finish(failed);
	}

	private void finish(final List<String> failed) {
		if (this.dialog != null) {
			this.dialog.dismiss();
		}

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
