package ch.rogerjaeggi.txt.loader;

import java.util.LinkedList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Rect;


public class TxtResult {

	private final Bitmap bitmap;
	
	private final List<TouchableArea> touchableAreas;
	
	public TxtResult(Bitmap bitmap) {
		this.bitmap = bitmap;
		this.touchableAreas = new LinkedList<TouchableArea>();
	}
	
	public Bitmap getBitmap() {
		return bitmap;
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
	
}
