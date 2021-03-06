package com.atsexp.fly.dialogs;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

import com.atsexp.fly.Browser;
import com.atsexp.fly.tasks.UnRarTask;
import com.atsexp.fly.tasks.UnZipTask;
import com.atsexp.fly.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;

public final class UnpackDialog extends DialogFragment {

	private static File file;
	private static String ext;

	public static DialogFragment instantiate(File file1) {
		file = file1;
		ext = FilenameUtils.getExtension(file1.getName());

		final UnpackDialog dialog = new UnpackDialog();
		return dialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle state) {
		final Activity a = getActivity();

		// Set an EditText view to get user input
		final EditText inputf = new EditText(a);
		inputf.setHint(R.string.enter_name);
		inputf.setText(Browser.mCurrentPath);

		final AlertDialog.Builder b = new AlertDialog.Builder(a);
		b.setTitle(R.string.extractto);
		b.setView(inputf);
		b.setPositiveButton(getString(R.string.ok),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						String newpath = inputf.getText().toString();

						dialog.dismiss();

						if (ext.equals("zip")) {
							final UnZipTask task = new UnZipTask(a);
							task.executeOnExecutor(
									AsyncTask.THREAD_POOL_EXECUTOR,
									file.getPath(), newpath);
						} else if (ext.equals("rar")) {
							final UnRarTask task = new UnRarTask(a);
							task.executeOnExecutor(
									AsyncTask.THREAD_POOL_EXECUTOR,
									file.getPath(), newpath);
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
