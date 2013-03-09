package ch.rogerjaeggi.txt.loader;

import static java.lang.Integer.valueOf;
import static java.util.Collections.sort;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
	
	public TouchableArea intersects(final Rect r) {
		List<TouchableArea> intersections = new ArrayList<TouchableArea>();
		for (TouchableArea area : pageInfo.getLinks()) {
			if (area.intersects(r)) {
				intersections.add(area);
			}
		}
		if (intersections.size() == 0) {
			return null;
		} else if (intersections.size() == 1) {
			return intersections.get(0);
		} else {
			sort(intersections, new Comparator<TouchableArea>() {

				@Override
				public int compare(TouchableArea lhs, TouchableArea rhs) {
					Rect intersectionLhs = lhs.intersect(r);
					Rect intersectionRhs = rhs.intersect(r);
					
					int sizeLhs = intersectionLhs.width() * intersectionLhs.height();
					int sizeRhs = intersectionRhs.width() * intersectionRhs.height();
					
					return valueOf(sizeRhs).compareTo(valueOf(sizeLhs));
				}
			});
			return intersections.get(0);
		}
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
