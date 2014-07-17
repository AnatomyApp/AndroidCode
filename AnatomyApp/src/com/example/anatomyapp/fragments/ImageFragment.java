package com.example.anatomyapp.fragments;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.anatomyapp.views.ScrollableImageView;
import com.example.anatomyapp4.R;

/**
 * Fragment to display images
 * @author js233
 *
 */
public class ImageFragment extends Fragment {
	public final static String OPT_POSITION = "position";
	public int mCurrentPosition = -1;
	
	private static final int PICK_FROM_CAMERA = 1;
	private static final int PICK_FROM_FILE = 2;

	private Uri mImageCaptureUri;
	private ScrollableImageView mImageView;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {

		// If activity recreated (such as from screen rotate), restore
		// the previous option selection set by onSaveInstanceState().
		// This is primarily necessary when in the two-pane layout.
		if (savedInstanceState != null) {
			mCurrentPosition = savedInstanceState.getInt(OPT_POSITION);
		}
		
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.image_view, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();

		Bundle args = getArguments();
		if (args != null) {
			// Set image based on argument passed in
			updateImageView(args.getInt(OPT_POSITION));
		} else if (mCurrentPosition != -1) {
			// Set image based on saved instance state defined during onCreateView
			updateImageView(mCurrentPosition);
		}
	}
	
	/**
	 * Choose image from device and display
	 * -- N.B. Take an image from camera does not work.
	 * @param position
	 */
	public void updateImageView(int position) {
		// Make sure the image view is cleared so that a new image is not placed on top of a previous image
		clearAllItems();
		final String [] items = new String [] {"From Camera", "From SD Card"};				
		ArrayAdapter<String> adapter = new ArrayAdapter<String> (this.getActivity(), android.R.layout.select_dialog_item,items);
		AlertDialog.Builder builder	= new AlertDialog.Builder(this.getActivity());

		builder.setTitle("Select Image");
		builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
			public void onClick( DialogInterface dialog, int item ) {
				if (item == 0) {
					// Search all images from those taken by camera
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					File file = new File(Environment.getExternalStorageDirectory(), "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
					mImageCaptureUri = Uri.fromFile(file);

					try {			
						intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
						intent.putExtra("return-data", true);
						System.out.println("Image captured from camera");

						startActivityForResult(intent, PICK_FROM_CAMERA);
					} catch (Exception e) {
						System.out.println("Unable to capture image from camera");
						e.printStackTrace();
					}			

					dialog.cancel();
				} 
				else {
					// Get image from the SD card
					Intent intent = new Intent();

					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);

					startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
					System.out.println("Image choosen from file");
				}
			}
		} );
		
		final AlertDialog dialog = builder.create();
		mImageView = new ScrollableImageView(this.getActivity());
		// Show dialog box asking where to get image from
		dialog.show();
	}
	
	/**
	 * Upon image selection transform into correct size of bitmap
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		decodeUri(data.getData());
		mImageView.bringToFront();
	}

	/**
	 * Scale image to a size of bitmap that fits inside the fragment
	 * @param uri
	 */
	public void decodeUri(Uri uri) {
		ParcelFileDescriptor parcelFD = null;
		try {
			parcelFD = this.getActivity().getContentResolver().openFileDescriptor(uri, "r");
			FileDescriptor imageSource = parcelFD.getFileDescriptor();

			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeFileDescriptor(imageSource, null, o);

			// the new size we want to scale to
			final int REQUIRED_SIZE = 1024;

			// Find the correct scale value. It should be the power of 2.
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE) {
					break;
				}
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			Bitmap bitmap = BitmapFactory.decodeFileDescriptor(imageSource, null, o2);
			
			mImageView.setLayoutParams(new LayoutParams(bitmap.getWidth()+4000, bitmap.getHeight()));
			mImageView.setImageBitmap(bitmap);
			ViewGroup root = (ViewGroup) this.getActivity().findViewById(R.id.layout1);
			
			// Add image to layout
			root.addView(mImageView);

		} catch (FileNotFoundException e) {
			// handle errors
		} catch (@SuppressWarnings("hiding") IOException e) {
			// handle errors
		} finally {
			if (parcelFD != null)
				try {
					parcelFD.close();
				} catch (IOException e) {
					System.out.println(e.toString());
				}
		}
	}
	
	/**
	 *  Zoom in on image by updating scale
	 */
	public void zoomIn() {
		float x = mImageView.getScaleX();
		float y = mImageView.getScaleY();

		mImageView.setScaleX((float) (x+1));
		mImageView.setScaleY((float) (y+1));
		
		System.out.println("Zooming in");
	}

	/**
	 *  Zoom out of image by updating scale
	 */
	public void zoomOut() {
		float x = mImageView.getScaleX();
		float y = mImageView.getScaleY();

		mImageView.setScaleX((float) (x-1));
		mImageView.setScaleY((float) (y-1));
		
		System.out.println("Zooming out");
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// Save the current option selection in case we need to recreate the fragment
		outState.putInt(OPT_POSITION, mCurrentPosition);
	}
	
	
	/**
	 * For testing purposes we have a series of images representing the different layers of a body part
	 * Here we load in the top layer
	 * 
	 */
	public void setTestImageZoom() {
		// Must clear image view to avoid image being placed on  top of another image
		clearAllItems();
		mImageView = new ScrollableImageView(this.getActivity());
		ViewGroup root = (ViewGroup) this.getActivity().findViewById(R.id.layout1);
		root.addView(mImageView);
		mImageView.setImageResource(R.drawable.real_hand1);
	}
	
	/**
	 * For testing purposes we have three images to be used for rotation
	 * One rotated to the right, one to the left and one central/face on
	 * Here we load the central/face on image
	 */
	public void setTestImageRotate() {
		// Must clear image view to avoid image being placed on  top of another image
		clearAllItems();
		mImageView = new ScrollableImageView(this.getActivity());
		ViewGroup root = (ViewGroup) this.getActivity().findViewById(R.id.layout1);
		root.addView(mImageView);
		mImageView.setImageResource(R.drawable.real_hand5);
	}
	
	/**
	 * For testing purposes we have three images to be used for rotation
	 * One rotated to the right, one to the left and one central/face on
	 * Here we load the central/face on image
	 */
	public void setCenterImage() {
		// Must clear image view to avoid image being placed on  top of another image
		clearAllItems();
		mImageView = new ScrollableImageView(this.getActivity());
		ViewGroup root = (ViewGroup) this.getActivity().findViewById(R.id.layout1);
		root.addView(mImageView);
		mImageView.setImageResource(R.drawable.real_hand5);
	}
	
	/**
	 * Set the image which shows the body part rotated to the right
	 */
	public void setRightImage() {
		// Must clear image view to avoid image being placed on  top of another image
		clearAllItems();
		mImageView = new ScrollableImageView(this.getActivity());
		ViewGroup root = (ViewGroup) this.getActivity().findViewById(R.id.layout1);
		root.addView(mImageView);
		mImageView.setImageResource(R.drawable.real_hand5_right);
	}
	
	/**
	 * Set the image which shows the body part rotated to the left
	 */
	public void setLeftImage() {
		// Must clear image view to avoid image being placed on  top of another image
		clearAllItems();
		mImageView = new ScrollableImageView(this.getActivity());
		ViewGroup root = (ViewGroup) this.getActivity().findViewById(R.id.layout1);
		root.addView(mImageView);
		mImageView.setImageResource(R.drawable.real_hand5_left);
	}
	
	
	/**
	 * Zoom through the layers of the body part the images show
	 * Set image view to image according to how far has been zoomed in or out
	 * @param imageId
	 */
	public void setLayeredImages(int imageId) {
		switch (imageId) {
			case 1:
				mImageView.setImageResource(R.drawable.real_hand1);
				break;
			case 2:
				mImageView.setImageResource(R.drawable.real_hand2);
				break;
			case 3:
				mImageView.setImageResource(R.drawable.real_hand3);
				break;
			case 4:
				mImageView.setImageResource(R.drawable.real_hand4);
				break;
			default:
				System.out.println("No image available");
				clearAllItems();
				return;
		}
	}
	
	/**
	 * Remove all items from image view
	 */
	public void clearAllItems() {
		ViewGroup vg = (ViewGroup) getActivity().findViewById(R.id.layout1);
		if(vg == null) {
			return;
		}
		if(mImageView == null) {
			return;
		}
		mImageView.setImageDrawable(null);		
		
		vg.removeAllViews();
		mImageView.invalidate();

	}
	
	/**
	 * Add a static hyperlink using a text view 
	 * This is to show that a link can be added
	 * However if you move the image the link stays on the same part of the page
	 */
	public void addHyperlink() {
		ViewGroup vg = (ViewGroup) getActivity().findViewById(R.id.layout1);
		if(vg == null) {
			return;
		}
		if(mImageView == null) {
			return;
		}
		
		TextView tv1 = new TextView(this.getActivity());
		String text = "This is just a test. Click this link here <a href=\"http://www.google.com\">Google</a> to visit google.";
		tv1.setMovementMethod(LinkMovementMethod.getInstance());
		tv1.setText(Html.fromHtml(text));
		tv1.setX(100);
		tv1.setY(100);
		vg.addView(tv1);
		tv1.bringToFront();
		
	}
	
	
}

