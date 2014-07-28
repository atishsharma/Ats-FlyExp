package com.atsexp.fly.settings;

import com.atsexp.fly.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

public final class Settings {

	public static boolean showthumbnail;
	public static boolean mShowHiddenFiles;
	public static int mListAppearance;
	public static int mSortType;
	public static int mTheme;
	public static String defaultdir;
	private static SharedPreferences p;

	public static void updatePreferences(Context context) {
		p = PreferenceManager.getDefaultSharedPreferences(context);

		mShowHiddenFiles = p.getBoolean("displayhiddenfiles", true);
		showthumbnail = p.getBoolean("showpreview", true);
		mTheme = Integer.parseInt(p.getString("preference_theme",
				Integer.toString(R.style.ThemeLight)));
		mSortType = Integer.parseInt(p.getString("sort", "1"));
		mListAppearance = Integer.parseInt(p.getString("viewmode", "1"));
		defaultdir = p.getString("defaultdir", Environment
				.getExternalStorageDirectory().getPath());
	}
}
