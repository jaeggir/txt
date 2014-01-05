package ch.rogerjaeggi.txt.menu;

import ch.rogerjaeggi.txt.loader.PageInfo;

import com.actionbarsherlock.view.MenuItem;


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
