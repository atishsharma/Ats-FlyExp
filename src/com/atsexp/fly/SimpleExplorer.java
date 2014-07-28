package com.atsexp.fly;

import com.atsexp.fly.settings.Settings;
import com.atsexp.fly.R;
import com.stericson.RootTools.RootTools;

import android.app.Application;
import android.os.Environment;
import android.widget.Toast;

public final class SimpleExplorer extends Application {

	public static final int THEME_ID_LIGHT = 1;
	public static final int THEME_ID_DARK = 2;

	public static boolean rootAccess;
	public static String busybox;

	@Override
	public void onCreate() {
		super.onCreate();
		// get default preferences
		Settings.updatePreferences(this);
		checkEnvironment();

		rootAccess = RootTools.isAccessGiven();
	}

	// check for external storage exists
	private void checkEnvironment() {
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);

		if (!sdCardExist) {
			Toast.makeText(this, getString(R.string.sdcardnotfound),
					Toast.LENGTH_SHORT).show();
		}
	}
}
