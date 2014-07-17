package com.example.anatomyapp.fragments;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.anatomyapp.Activities.MainActivity;
import com.example.anatomyapp4.R;


/**
 * Fragment to display a menu-style list of options
 * @author J_Spyt
 *
 */
public class OptionsFragment extends ListFragment {

	private OnOptionSelectedListener mCallback;
	private int layout;
	
	private final int OPTION1 = 0;
	private final int OPTION2 = 1;
	private final int OPTION3 = 2;
	private final int OPTION4 = 3;

	// First Option
	static String[] SelectImage = {
		"Select Image",
		"Test Image for Zoom",
		"Test Image for Rotate"
	};

	// Options for when test image is selected
	static String[] TestImageZoomOptions = {
		"Zoom In +",
		"Zoom Out -",
		"Add Hyperlink",
		"Clear All"
	};
	
	// Options for when test image is selected
	static String[] TestImageRotateOptions = {
		"Rotate Right",
		"Rotate Left",
		"Add Hyperlink",
		"Clear All"
	};

	// Options for when an image has been selected from SD card
	static String[] SDCardOptions = {
		"Zoom In +",
		"Zoom Out -",
		"Add Hyperlink",
		"Clear All"
	};

	/*
	 * Interface of options to choose from.
	 * Each option must be implemented by MainActivity
	 */
	public interface OnOptionSelectedListener {
		public void onTestImageZoom();
		public void onTestImageRotate();
		public void onImageSelected(int position);
		public void onImageZoomIn();
		public void onImageZoomOut();
		public void onRotateRight();
		public void onRotateLeft();
		public void onAddHyperlink();
		public void onClearAll();
	}

	/**
	 * On create of fragment
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// We need to use a different list item layout for devices older than Honeycomb
		layout = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
				android.R.layout.simple_list_item_activated_1 : android.R.layout.simple_list_item_1;

		// Has an image been selected? Display options appropriately
		setListAdapter(new ArrayAdapter<String>(getActivity(), layout, SelectImage));
	}

	/**
	 * On start of fragment
	 * Make sure fragment exists in main layout before loading menu
	 */
	@Override
	public void onStart() {
		super.onStart();
		if (getFragmentManager().findFragmentById(R.id.image_fragment) != null) {
			getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		}
	}

	/**
	 * Called before on create
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception.
		try {
			mCallback = (OnOptionSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnOptionSelectedListener");
		}
	}

	/**
	 * Treat like a listener
	 * Responds to list fragment menu selections
	 */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		
		// Setters and Getters do not work and so static boolean fields are being used
		if(MainActivity.isImageSelected == true) {
			
			if(position == OPTION1) {
				mCallback.onImageZoomIn();
			}

			if(position == OPTION2) {
				mCallback.onImageZoomOut();
			}

			if(position == OPTION3) {
				mCallback.onAddHyperlink();
			}

			if(position == OPTION4) {
				mCallback.onClearAll();
				setListAdapter(new ArrayAdapter<String>(getActivity(), layout, SelectImage));
			}
		}
		
		else if(MainActivity.isTestImageZoomSelected == true) {
			if(position == OPTION1) {
				mCallback.onImageZoomIn();
			}

			if(position == OPTION2) {
				mCallback.onImageZoomOut();
			}
			
			if(position == OPTION3) {
				mCallback.onAddHyperlink();
			}
			
			if(position == OPTION4) {
				mCallback.onClearAll();
				setListAdapter(new ArrayAdapter<String>(getActivity(), layout, SelectImage));
			}
		}
		
		else if(MainActivity.isTestImageRotateSelected == true) {
			if(position == OPTION1) {
				mCallback.onRotateRight();
			}
			
			if(position == OPTION2) {
				mCallback.onRotateLeft();
			}
			
			if(position == OPTION3) {
				mCallback.onAddHyperlink();
			}
			
			if(position == OPTION4) {
				mCallback.onClearAll();
				setListAdapter(new ArrayAdapter<String>(getActivity(), layout, SelectImage));
			}
		}
		
		else if(MainActivity.isClearSelected == true){
			// Load a selected image
			if(position == OPTION1) {
				mCallback.onImageSelected(position);
				setListAdapter(new ArrayAdapter<String>(getActivity(), layout, SDCardOptions));
			}
			
			// Load test image for zoom
			if(position == OPTION2) {
				mCallback.onTestImageZoom();
				setListAdapter(new ArrayAdapter<String>(getActivity(), layout, TestImageZoomOptions));
			}
			
			if(position == OPTION3) {
				mCallback.onTestImageRotate();
				setListAdapter(new ArrayAdapter<String>(getActivity(), layout, TestImageRotateOptions));
			}
			if(position == OPTION4) {
				setListAdapter(new ArrayAdapter<String>(getActivity(), layout, SelectImage));
			}
		}
		
		else {
			// Load a selected image
			if(position == OPTION1) {
				mCallback.onImageSelected(position);
				setListAdapter(new ArrayAdapter<String>(getActivity(), layout, SDCardOptions));
			}
			
			// Load test image for zoom
			if(position == OPTION2) {
				mCallback.onTestImageZoom();
				setListAdapter(new ArrayAdapter<String>(getActivity(), layout, TestImageZoomOptions));
			}
			
			if(position == OPTION3) {
				mCallback.onTestImageRotate();
				setListAdapter(new ArrayAdapter<String>(getActivity(), layout, TestImageRotateOptions));
			}
		}

		// Set the item as checked to be highlighted when in two-pane layout
		getListView().setItemChecked(position, true);
	}
}
