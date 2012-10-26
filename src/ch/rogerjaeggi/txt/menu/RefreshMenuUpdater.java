package ch.rogerjaeggi.txt.menu;

import com.actionbarsherlock.view.MenuItem;


public class RefreshMenuUpdater implements MenuUpdater {

	private final MenuItem item;
	
	public RefreshMenuUpdater(MenuItem item) {
		this.item = item;
	}
	
	@Override
	public void update(int page) {
		item.setEnabled(true);
	}
	
	@Override
	public MenuItem getMenuItem() {
		return item;
	}
}
