package com.example.anatomyapp.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.anatomyapp.fragments.ImageFragment;
import com.example.anatomyapp.fragments.OptionsFragment;
import com.example.anatomyapp4.R;

/**
 * Main Activity to display opening screen
 * @author js233
 *
 */
public class MainActivity extends FragmentActivity
implements OptionsFragment.OnOptionSelectedListener{


	private static final int RESULT_SETTINGS = 1;
	public static boolean isImageSelected = false;
	public static boolean isTestImageZoomSelected = false;
	public static boolean isTestImageRotateSelected = false;
	public static boolean isClearSelected = false;

	// Count to know how far we have zoomed in or out
	private int zoomCount = 0;
	/*
	 * Count to know if we are rotating left or right and by how much
	 * Positive right / Negative left
	 */
	private int rotateCount = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Check whether the activity is using the layout version with
		// the fragment_container FrameLayout. If so, we must add the first fragment
		if (findViewById(R.id.fragment_container) != null) {

			// However, if we're being restored from a previous state,
			// then we don't need to do anything and should return or else
			// we could end up with overlapping fragments.
			if (savedInstanceState != null) {
				return;
			}

			// Create an instance of OptionFragment
			OptionsFragment optionsFragment = new OptionsFragment();
			Log.d("MainActivity", "Options menu fragment created");

			// In case this activity was started with special instructions from an Intent,
			// pass the Intent's extras to the fragment as arguments
			optionsFragment.setArguments(getIntent().getExtras());

			// Add the fragment to the 'fragment_container' FrameLayout
			getSupportFragmentManager().beginTransaction()
			.add(R.id.fragment_container, optionsFragment).commit();
		}
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

	/**
	 * Options for overflow menu
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.home) {
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
	 * Click on setting in the overflow menu and this code will run
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


	/**
	 * Set imageView to display the 1st(top layer) image
	 * This is so that when you zoom in you can zoom through each of the layers of the hand
	 * starting at the top and moving down
	 */
	public void onTestImageZoom() {
		// Capture the ImageFragment from the activity layout
		ImageFragment imageFrag = (ImageFragment)
				getSupportFragmentManager().findFragmentById(R.id.image_fragment);

		if (imageFrag != null) {
			// Call a method in the ImageFragment to update its content
			imageFrag.setTestImageZoom();
			isTestImageZoomSelected = true;
			isClearSelected = false;
		}
	}

	/**
	 * Set imageView to display the image provided for rotation
	 * This image must be used for testing as it is at the correct layer
	 * the image taken both rotated right and rotated left
	 */
	public void onTestImageRotate() {
		// Capture the ImageFragment from the activity layout
		ImageFragment imageFrag = (ImageFragment)
				getSupportFragmentManager().findFragmentById(R.id.image_fragment);

		if (imageFrag != null) {
			imageFrag.setTestImageRotate();
			isTestImageRotateSelected = true;
			isClearSelected = false;
		}
	}


	/**
	 * Choose image to load from those already on device
	 */
	public void onImageSelected(int position) {
		// Capture the ImageFragment from the activity layout
		ImageFragment imageFrag = (ImageFragment)
				getSupportFragmentManager().findFragmentById(R.id.image_fragment);

		if (imageFrag != null) {

			// Call a method in the ImageFragment to update its content
			imageFrag.updateImageView(position);
			isImageSelected = true;

		}
		else {
			// If the image fragment is not available, we're in the one-pane layout and must swap fragments

			// Create fragment and give it an argument for the selected option
			ImageFragment newFragment = new ImageFragment();
			Bundle args = new Bundle();
			args.putInt(ImageFragment.OPT_POSITION, position);
			newFragment.setArguments(args);
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

			// Replace whatever is in the fragment_container view with this fragment,
			// and add the transaction to the back stack so the user can navigate back
			transaction.replace(R.id.fragment_container, newFragment);
			transaction.addToBackStack(null);

			// Commit the transaction
			transaction.commit();
			//isClearSelected = false;
			isImageSelected = true;
		}
	}

	/**
	 * Zoom in on image dependent on which image has been selected
	 * If the zoom image has been selected, zoom will go through the layers
	 * otherwise it will zoom in on only the one image
	 */
	public void onImageZoomIn() {
		if(isTestImageZoomSelected == true) {
			testImageZoomIn();
		}
		else {
			imageZoomIn();
		}
	}


	/**
	 * If we have selected the zoom test image, zoom in will move through all the images
	 */
	private void testImageZoomIn() {
		// Capture the ImageFragment from the activity layout
		ImageFragment imageFrag = (ImageFragment)
				getSupportFragmentManager().findFragmentById(R.id.image_fragment);

		if (imageFrag != null) {
			switch(zoomCount) {
			case 3:
				imageFrag.setLayeredImages(2);
				for(int i = 0; i < 4; i++) {
					imageFrag.zoomOut();
				}
				break;
			case 6:
				imageFrag.setLayeredImages(3);
				for(int i = 0; i < 4; i++) {
					imageFrag.zoomOut();
				}
				break;
			case 9:
				imageFrag.setLayeredImages(4);
				for(int i = 0; i < 4; i++) {
					imageFrag.zoomOut();
				}
				break;
			default:

			}
			imageFrag.zoomIn();
			zoomCount++;
		}
	}

	/**
	 * Zoom in normally on the one image
	 */
	private void imageZoomIn() {
		// Capture the ImageFragment from the activity layout
		ImageFragment imageFrag = (ImageFragment)
				getSupportFragmentManager().findFragmentById(R.id.image_fragment);

		imageFrag.zoomIn();
	}


	/**
	 * Zoom out on image dependent on which image has been selected
	 * If the zoom image has been selected, zoom will go through the layers
	 * otherwise it will zoom out on only the one image
	 */
	public void onImageZoomOut() {
		if(isTestImageZoomSelected == true) {
			testImageZoomOut();
		}
		else {
			imageZoomOut();
		}
	}

	/**
	 * If we have selected the zoom test image, zoom out will move through all the images
	 */
	private void testImageZoomOut() {
		// Capture the ImageFragment from the activity layout
		ImageFragment imageFrag = (ImageFragment)
				getSupportFragmentManager().findFragmentById(R.id.image_fragment);

		if (imageFrag != null) {
			switch(zoomCount) {
			case 10:
				imageFrag.setLayeredImages(3);
				for(int i = 0; i < 3; i++) {
					imageFrag.zoomIn();
				}
				break;
			case 7:
				imageFrag.setLayeredImages(2);
				for(int i = 0; i < 3; i++) {
					imageFrag.zoomIn();
				}
				break;
			case 4:
				imageFrag.setLayeredImages(1);
				for(int i = 0; i < 3; i++) {
					imageFrag.zoomIn();
				}
				break;
			default:
				break;
			}
			imageFrag.zoomOut();
			zoomCount--;
		}
	}

	/**
	 * Zoom out normally on the one image
	 */
	private void imageZoomOut() {
		ImageFragment imageFrag = (ImageFragment)
				getSupportFragmentManager().findFragmentById(R.id.image_fragment);

		imageFrag.zoomOut();
	}

	/**
	 * Rotate the image right by loading in the correct image at the correct angle
	 * 0 - represents the image being central or face on
	 */
	public void onRotateRight() {
		// Capture the ImageFragment from the activity layout
		ImageFragment imageFrag = (ImageFragment)
				getSupportFragmentManager().findFragmentById(R.id.image_fragment);

		if (imageFrag != null) {
			rotateCount++;
			if(rotateCount == 0) {
				imageFrag.setCenterImage();
			}
			else {
				imageFrag.setRightImage();
			}
		}
	}

	/**
	 * Rotate the image right by loading in the correct image at the correct angle
	 * 0 - represents the image being central or face on
	 */
	public void onRotateLeft() {
		// Capture the ImageFragment from the activity layout
		ImageFragment imageFrag = (ImageFragment)
				getSupportFragmentManager().findFragmentById(R.id.image_fragment);

		if (imageFrag != null) {
			rotateCount--;
			if(rotateCount == 0) {
				imageFrag.setCenterImage();
			}
			else {
			imageFrag.setLeftImage();
			}
		}

	}

	/**
	 * This will add a static hyperlink in the form of a text view
	 */
	public void onAddHyperlink() {
		// Capture the ImageFragment from the activity layout
		ImageFragment imageFrag = (ImageFragment)
				getSupportFragmentManager().findFragmentById(R.id.image_fragment);

		if (imageFrag != null) {
			imageFrag.addHyperlink();
		}
	}

	/**
	 * Clear everything from image fragment and reset menu on list fragment
	 */
	public void onClearAll() {
		// Capture the ImageFragment from the activity layout
		ImageFragment imageFrag = (ImageFragment)
				getSupportFragmentManager().findFragmentById(R.id.image_fragment);

		if (imageFrag != null) {
			// Call a method in the ImageFragment to clear everything from view
			imageFrag.clearAllItems();
			zoomCount = 0;
			isTestImageZoomSelected = false;
			isTestImageRotateSelected = false;
			isImageSelected = false;
			isClearSelected = true;
		}
	}

	/**
	 * Display help screen
	 */
	public void showHelp() {
		Intent intent = new Intent(this, HelpActivity.class);
		startActivity(intent);
	}
}
