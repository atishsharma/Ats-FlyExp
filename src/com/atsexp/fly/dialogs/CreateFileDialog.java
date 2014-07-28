package com.atsexp.fly.dialogs;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.atsexp.fly.Browser;
import com.atsexp.fly.commands.RootCommands;
import com.atsexp.fly.R;
import com.stericson.RootTools.RootTools;

public final class CreateFileDialog extends DialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Activity a = getActivity();

		// Set an EditText view to get user input
		final EditText inputf = new EditText(a);
		inputf.setHint(R.string.enter_name);

		final AlertDialog.Builder b = new AlertDialog.Builder(a);
		b.setTitle(R.string.newfile);
		b.setView(inputf);
		b.setPositiveButton(R.string.create,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String name = inputf.getText().toString();

						File file = new File(Browser.mCurrentPath
								+ File.separator + name);

						if (file.exists()) {
							Toast.makeText(a, getString(R.string.fileexists),
									Toast.LENGTH_SHORT).show();
						} else {
							try {
								if (name.length() >= 1) {
									file.createNewFile();

									Toast.makeText(a, R.string.filecreated,
											Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(a, R.string.error,
											Toast.LENGTH_SHORT).show();
								}
							} catch (Exception e) {
								if (RootTools.isRootAvailable()) {
									RootCommands.createRootFile(
											Browser.mCurrentPath, name);
									Toast.makeText(a, R.string.filecreated,
											Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(a, R.string.error,
											Toast.LENGTH_SHORT).show();
								}
							}
						}

						dialog.dismiss();
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
