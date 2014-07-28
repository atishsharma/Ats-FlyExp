package com.atsexp.fly.settings;

import com.atsexp.fly.Browser;
import com.atsexp.fly.ThemableActivity;
import com.atsexp.fly.R;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

public class SettingsActivity extends ThemableActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.show();
	}

	@Override
	public void onBackPressed() {
		this.finish();
		Intent i = new Intent(getBaseContext(), Browser.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
		return;
	}

	void proxyRestart() {
		restart();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menu) {
		switch (menu.getItemId()) {

		case android.R.id.home:
			this.finish();
			Intent i = new Intent(getBaseContext(), Browser.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			return true;
		}
		return false;
	}
}