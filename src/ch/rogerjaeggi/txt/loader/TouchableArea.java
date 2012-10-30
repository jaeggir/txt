package ch.rogerjaeggi.txt.loader;

import android.graphics.Rect;


public class TouchableArea {

	private final Rect area;

	private final int target;
	
	public TouchableArea(Rect area, int target) {
		this.area = area;
		this.target = target;
	}
	
	public boolean intersects(Rect r) {
		return Rect.intersects(area, r);
	}
	
	public int getTarget() {
		return target;
	}

}
