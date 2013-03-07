package ch.rogerjaeggi.txt.ui;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;


public class PageTouchListener implements OnTouchListener {

	private final GestureDetector gestureDetector;
	
	public PageTouchListener(PageActivity pageActivity, AbstractPageGestureListener gestureDetector) {
		this.gestureDetector = new GestureDetector(pageActivity, gestureDetector);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}

}
