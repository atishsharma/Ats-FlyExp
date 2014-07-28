package com.atsexp.fly;

import com.atsexp.fly.settings.Settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public abstract class ThemableActivity extends Activity {

	protected static final String EXTRA_SAVED_STATE = "ThemableActivity.extras.SAVED_STATE";

	private int mCurrentTheme;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mCurrentTheme = Settings.mTheme;
		if (setThemeInOnCreate()) {
			setTheme(mCurrentTheme);
		}
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mCurrentTheme != Settings.mTheme) {
			restart();
		}
	}

	protected void restart() {
		final Bundle outState = new Bundle();
		onSaveInstanceState(outState);
		final Intent intent = new Intent(this, getClass());
		intent.putExtra(EXTRA_SAVED_STATE, outState);
		finish();
		overridePendingTransition(0, 0);
		startActivity(intent);
	}

	protected boolean setThemeInOnCreate() {
		return true;
	}
}
