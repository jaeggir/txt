package ch.rogerjaeggi.txt.loader;

import android.graphics.Rect;


public class TouchableArea {

	private final Rect area;

	private final int page;
	
	private final int subPage;
	
	public TouchableArea(Rect area, int page, int subPage) {
		this.area = area;
		this.page = page;
		this.subPage = subPage;
	}
	
	public boolean intersects(Rect r) {
		return Rect.intersects(area, r);
	}
	
	public Rect intersect(Rect r) {
		Rect result = new Rect(area);
		result.intersect(r);
		return result;
	}
	
	public int getPage() {
		return page;
	}
	
	public int getSubPage() {
		return subPage;
	}

}
