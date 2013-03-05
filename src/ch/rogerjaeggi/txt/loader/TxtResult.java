package ch.rogerjaeggi.txt.loader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Rect;
import ch.rogerjaeggi.txt.loader.cache.TxtKey;
import ch.rogerjaeggi.utils.Logging;


public class TxtResult {

	private static final long MAX_AGE = 5 * 60 * 1000l;
	
	private final TxtKey key;
	
	private final long timestamp;
	
	private final Bitmap bitmap;
	
	private final Exception exception;
	
	private final List<TouchableArea> touchableAreas;
	
	public TxtResult(TxtKey key, Bitmap bitmap) {
		this.key = key;
		this.bitmap = bitmap;
		this.touchableAreas = new LinkedList<TouchableArea>();
		this.timestamp = System.currentTimeMillis();
		this.exception = null;
	}

	// TODO think about using error codes
	public TxtResult(TxtKey key, Exception exception) {
		this.key = key;
		this.exception = exception;
		this.bitmap = null;
		this.touchableAreas = new LinkedList<TouchableArea>();
		this.timestamp = System.currentTimeMillis();
	}
	
	public TxtKey getKey() {
		return key;
	}
	
	public Bitmap getBitmap() {
		return bitmap;
	}

	public Exception getError() {
		return exception;
	}
	
	public TouchableArea intersects(Rect r) {
		for (TouchableArea area : touchableAreas) {
			if (area.intersects(r)) {
				return area;
			}
		}
		return null;
	}

	public void addTouchableAreas(List<TouchableArea> areas) {
		if (areas != null) {
			touchableAreas.addAll(areas);
		}
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
	
}
