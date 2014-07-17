package com.example.anatomyapp.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.anatomyapp4.R;

/**
 * A page to display text describing how to use the application
 * @author js233
 *
 */
public class HelpActivity extends Activity {
	
	private static final int RESULT_SETTINGS = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Options provided in the overflow menu
	 * Run when selected
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.home) {
			showHome();
			return true;
		}
		else if (itemId == R.id.help) {
			return true;
		}
		else if (itemId == R.id.action_settings) {
			Intent i = new Intent(this, UserSettingActivity.class);
			startActivityForResult(i, RESULT_SETTINGS);
			return true;
		} 
		else {
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * The user may select the setting page
	 * This will inflate that page on result
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
		case RESULT_SETTINGS:
			showUserSettings();
			break;
		}
	}

	/**
	 * Run the main activity to open the initial screen
	 * An intent is required to start a new activity when already running another
	 */
	public void showHome() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}
	
	/**
	 * Open a default settings page
	 */
	private void showUserSettings() {
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("\n Username: " + sharedPrefs.getString("prefUsername", "NULL"));
		builder.append("\n Send report: " + sharedPrefs.getBoolean("prefSendReport", false));
		builder.append("\n Sync Frequency: " + sharedPrefs.getString("prefSyncFrequency", "NULL"));
		
		TextView settingsTextView = (TextView) findViewById(R.id.textUserSettings);
		
		settingsTextView.setText(builder.toString());
	}

}
