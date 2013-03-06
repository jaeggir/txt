package ch.rogerjaeggi.txt.loader;

import java.io.FileNotFoundException;
import java.io.IOException;
import android.graphics.Bitmap;
import android.graphics.Rect;
import ch.rogerjaeggi.utils.Logging;


public class TxtResult {

	private PageInfo pageInfo;
	
	private final Bitmap bitmap;
	
	private static final long MAX_AGE = 5 * 60 * 1000l;
		
	private final long timestamp;
	
	private final Exception exception;	
	
	public TxtResult(PageInfo pageInfo, Bitmap bitmap) {
		this.pageInfo = pageInfo;
		this.bitmap = bitmap;
		this.timestamp = System.currentTimeMillis();
		this.exception = null;
	}

	// TODO think about using error codes
//	public TxtResult(TxtKey key, Exception exception) {
//		this.key = key;
//		this.exception = exception;
//		this.bitmap = null;
//		this.timestamp = System.currentTimeMillis();
//	}
	
	public PageInfo getPageInfo() {
		return pageInfo;
	}
	
	public Bitmap getBitmap() {
		return bitmap;
	}

	public Exception getError() {
		return exception;
	}
	
	public TouchableArea intersects(Rect r) {
		for (TouchableArea area : pageInfo.getLinks()) {
			if (area.intersects(r)) {
				return area;
			}
		}
		return null;
	}

	public boolean isValid() {
		if (bitmap == null) {
			Logging.d(this, "isValid(): bitmap is null, returning true");
			return true;
		} else {
			boolean expired = (timestamp + MAX_AGE) < System.currentTimeMillis();
			boolean result = !bitmap.isRecycled() && !expired;
			Logging.d(this, "isValid(): isRecycled=" + bitmap.isRecycled() + ", expired=" + expired + ", returning " + result);
			return result;
		}
	}
	
	public boolean isConnectionError() {
		if (exception == null) {
			return false;
		} else {
			return exception instanceof IOException;
		}
	}
	
	public boolean isPageDoesNotExistError() {
		if (exception == null) {
			return false;
		} else {
			return exception instanceof FileNotFoundException;
		}
	}

	public void setPageInfo(PageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}
	
}
