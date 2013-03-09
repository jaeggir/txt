package ch.rogerjaeggi.txt.ui;

import static ch.rogerjaeggi.txt.Constants.GO_TO_CODE;
import static ch.rogerjaeggi.txt.loader.PageKeyFactory.getNextPageKey;
import static ch.rogerjaeggi.txt.loader.PageKeyFactory.getNextSubPageKey;
import static ch.rogerjaeggi.txt.loader.PageKeyFactory.getPageKey;
import static ch.rogerjaeggi.txt.loader.PageKeyFactory.getPreviousPageKey;
import static ch.rogerjaeggi.txt.loader.PageKeyFactory.getPreviousSubPageKey;
import android.content.Intent;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import ch.rogerjaeggi.txt.loader.PageInfo;
import ch.rogerjaeggi.utils.Logging;


public abstract class AbstractPageGestureListener extends SimpleOnGestureListener {

	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	
	private final PageActivity pageActivity;
	
	private final PageInfo pageInfo;

	private final int swipeMinDistance;
	
	public AbstractPageGestureListener(PageActivity pageActivity, PageInfo pageInfo, int swipeMinDistance) {
		this.pageActivity = pageActivity;
		this.pageInfo = pageInfo;
		this.swipeMinDistance = swipeMinDistance;
	}

	protected void handleSimpleClick() {
		Intent intent = new Intent(pageActivity, GoToActivity.class);
		pageActivity.startActivityForResult(intent, GO_TO_CODE);
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		try {
			if (e1.getX() - e2.getX() > swipeMinDistance && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				if (pageInfo.hasNextPage()) {
					pageActivity.requestPage(getNextPageKey(pageInfo));
					return true;
				}
			} else if (e2.getX() - e1.getX() > swipeMinDistance && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				if (pageInfo.hasPreviousPage()) {
					pageActivity.requestPage(getPreviousPageKey(pageInfo));
					return true;
				}
			} else if (e1.getY() - e2.getY() > swipeMinDistance && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
				if (pageInfo.hasPreviousSubPage()) {
					pageActivity.requestPage(getPreviousSubPageKey(pageInfo));
					return true;
				}
			} else if (e2.getY() - e1.getY() > swipeMinDistance && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
				if (pageInfo.hasNextSubPage()) {
					pageActivity.requestPage(getNextSubPageKey(pageInfo));
					return true;
				}
			}
		} catch (Exception e) {
			Logging.d(this, "Swipe failed.", e);
		}
		return false;
	}

    @Override
	public boolean onDoubleTap(MotionEvent event) {
    	pageActivity.requestPage(getPageKey(pageInfo, true));
		return true;
    }
}
