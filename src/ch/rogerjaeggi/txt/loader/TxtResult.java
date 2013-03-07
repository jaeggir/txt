package ch.rogerjaeggi.txt.loader;

import android.graphics.Bitmap;
import android.graphics.Rect;
import ch.rogerjaeggi.utils.Logging;


public class TxtResult {

	private PageInfo pageInfo;
	
	private final Bitmap bitmap;
		
	private final long timestamp;
	
	public TxtResult(PageInfo pageInfo, Bitmap bitmap) {
		this.pageInfo = pageInfo;
		this.bitmap = bitmap;
		this.timestamp = System.currentTimeMillis();
	}
	
	public PageInfo getPageInfo() {
		return pageInfo;
	}
	
	public Bitmap getBitmap() {
		return bitmap;
	}
	
	public TouchableArea intersects(Rect r) {
		for (TouchableArea area : pageInfo.getLinks()) {
			if (area.intersects(r)) {
				return area;
			}
		}
		return null;
	}

	public boolean isValid(long maxAge) {
		if (bitmap == null) {
			Logging.d(this, "isValid(): bitmap is null, returning true");
			return true;
		} else {
			boolean expired = (timestamp + maxAge) < System.currentTimeMillis();
			boolean result = !bitmap.isRecycled() && !expired;
			Logging.d(this, "isValid(): isRecycled=" + bitmap.isRecycled() + ", expired=" + expired + ", returning " + result);
			return result;
		}
	}

	public void setPageInfo(PageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}
	
}
