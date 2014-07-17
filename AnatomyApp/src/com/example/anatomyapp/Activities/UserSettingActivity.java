package com.example.anatomyapp.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.anatomyapp4.R;

/**
 * Settings page
 * @author js233
 *
 */
public class UserSettingActivity extends PreferenceActivity {
	
	private static final int RESULT_SETTINGS = 1;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.settings);
	}

	/**
	 * Create android menu - will appear in overflow
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.home) {
			showHome();
			return true;
		}
		else if (itemId == R.id.help) {
			showHelp();
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
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
		case RESULT_SETTINGS:
			showUserSettings();
			break;
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_user_setting,
					container, false);
			return rootView;
		}
	}
	
	public void showHelp() {
		Intent intent = new Intent(this, HelpActivity.class);
		startActivity(intent);
	}
	
	public void showHome() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}
	
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
