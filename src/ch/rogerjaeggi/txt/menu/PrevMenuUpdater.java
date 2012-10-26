package ch.rogerjaeggi.txt.menu;

import com.actionbarsherlock.view.MenuItem;


public class PrevMenuUpdater implements MenuUpdater {

	private final MenuItem item;
	
	public PrevMenuUpdater(MenuItem item) {
		this.item = item;
	}
	
	@Override
	public void update(int page) {
		item.setEnabled(page > 100);
	}
	
	@Override
	public MenuItem getMenuItem() {
		return item;
	}

}
