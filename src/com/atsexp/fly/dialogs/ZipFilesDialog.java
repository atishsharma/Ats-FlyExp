package com.atsexp.fly.dialogs;

import java.io.File;

import com.atsexp.fly.Browser;
import com.atsexp.fly.tasks.ZipFolderTask;
import com.atsexp.fly.tasks.ZipTask;
import com.atsexp.fly.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

public final class ZipFilesDialog extends DialogFragment {

	private static String[] files;

	public static DialogFragment instantiate(String[] files1) {
		files = files1;

		final ZipFilesDialog dialog = new ZipFilesDialog();
		return dialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle state) {
		final Activity a = getActivity();
		final String zipfile = Browser.mCurrentPath + "/" + "zipfile.zip";
		final int size = files.length;

		// Set an EditText view to get user input
		final EditText inputf = new EditText(a);
		inputf.setHint(R.string.enter_name);
		inputf.setText(zipfile);

		final AlertDialog.Builder b = new AlertDialog.Builder(a);
		b.setTitle(getString(R.string.packing) + " (" + String.valueOf(size)
				+ ")");
		b.setView(inputf);
		b.setPositiveButton(getString(R.string.ok),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String newpath = inputf.getText().toString();
						File file = new File(newpath);

						if (file.exists()) {
							Toast.makeText(a, a.getString(R.string.fileexists),
									Toast.LENGTH_SHORT).show();
							return;
						}

						if (files.length == 1) {
							File test = new File(files[0]);
							if (test.isDirectory()) {
								dialog.dismiss();
								final ZipFolderTask task = new ZipFolderTask(a,
										newpath);
								task.executeOnExecutor(
										AsyncTask.THREAD_POOL_EXECUTOR,
										files[0]);
							} else {
								dialog.dismiss();
								final ZipTask task = new ZipTask(a, newpath);
								task.executeOnExecutor(
										AsyncTask.THREAD_POOL_EXECUTOR, files);
							}
						} else {
							dialog.dismiss();
							final ZipTask task = new ZipTask(a, newpath);
							task.executeOnExecutor(
									AsyncTask.THREAD_POOL_EXECUTOR, files);
						}
					}
				});
		b.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		return b.create();
	}
}
