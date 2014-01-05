package ch.rogerjaeggi.txt.menu;

import android.view.MenuItem;

import ch.rogerjaeggi.txt.loader.PageInfo;


public class RefreshMenuUpdater implements MenuUpdater {

	private final MenuItem item;
	
	public RefreshMenuUpdater(MenuItem item) {
		this.item = item;
	}
	
	@Override
	public void update(PageInfo pageInfo) {
		item.setEnabled(true);
	}
	
	@Override
	public MenuItem getMenuItem() {
		return item;
	}
}
