package com.example.anatomyapp.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Scroller;

/**
 * Custom view to enable image in image view to be moved around upon touch
 * @author js233
 *
 */
public class ScrollableImageView extends ImageView {

	private GestureDetectorCompat gestureDetector;
	private Scroller scroller;
	private Paint mTextPaint;

	private int positionX = 0;
	private int positionY = 0;

	private final int HORIZONTAL_OFFSET = 100;
	private final int VERTICAL_OFFSET = 400;

	/**
	 * Constructor to initialise touch and scroll ability
	 * @param context
	 */
	public ScrollableImageView(Context context) {
		super(context);
		mTextPaint = new Paint();

		gestureDetector = new GestureDetectorCompat(context, gestureListener);
		scroller = new Scroller(context);
	}

	/**
	 * Touch gesture
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gestureDetector.onTouchEvent(event);
		return true;
	}

	/**
	 * Compute where the user would wish to scroll too
	 */
	@Override
	public void computeScroll() {
		super.computeScroll();

		if (scroller.computeScrollOffset()) {
			positionX = scroller.getCurrX();
			positionY = scroller.getCurrY();
			scrollTo(positionX, positionY);
		}
	}

	/**
	 * Find horizontal bounds
	 * @return
	 */
	private int getMaxHorizontal() {
		return (getDrawable().getBounds().width()-HORIZONTAL_OFFSET);
	}

	/**
	 * Find vertical bounds
	 * @return
	 */
	private int getMaxVertical() {
		return (getDrawable().getBounds().height()-VERTICAL_OFFSET);
	}

	private SimpleOnGestureListener gestureListener = new SimpleOnGestureListener() {

		@Override
		public boolean onDown(MotionEvent e) {
			ViewCompat.postInvalidateOnAnimation(ScrollableImageView.this);
			return true;
		}

		/**
		 * Scroll image to new position
		 */
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			scroller.forceFinished(true);
			// Normalise scrolling distances to not over scroll the image
			int dx = (int) distanceX;
			int dy = (int) distanceY;
			int newPositionX = positionX + dx;
			int newPositionY = positionY + dy;
			if (newPositionX < 0) {
			}
			else if (newPositionX > getMaxHorizontal()) {
				dx -= (newPositionX - getMaxHorizontal());
			}
			else if (newPositionY < 0) {
			}
			else if (newPositionY > getMaxVertical()) {
				dy -= (newPositionY - getMaxVertical());
			}
			scroller.startScroll(positionX, positionY, dx, dy, 0);
			ViewCompat.postInvalidateOnAnimation(ScrollableImageView.this);
			return true;
		}
	};


	/**
	 * When image is first displayed, onDraw is called
	 * ---Entry point to display labels on image---
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		mTextPaint.setTextSize(32);
		
		canvas.drawText("BODY PART", 400, 100, mTextPaint);
		canvas.drawText("BODY PART", 300, 200, mTextPaint);
		canvas.drawText("BODY PART", 400, 300, mTextPaint);
		canvas.drawText("BODY PART", 300, 400, mTextPaint);
		canvas.drawText("BODY PART", 400, 500, mTextPaint);
		canvas.rotate(1000);
	}
}
