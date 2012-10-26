package ch.rogerjaeggi.txt.menu;

import com.actionbarsherlock.view.MenuItem;


public class NextMenuUpdater implements MenuUpdater {

	private final MenuItem item;
	
	public NextMenuUpdater(MenuItem item) {
		this.item = item;
	}
	
	@Override
	public void update(int page) {
		item.setEnabled(page < 899);
	}
	
	@Override
	public MenuItem getMenuItem() {
		return item;
	}

}
