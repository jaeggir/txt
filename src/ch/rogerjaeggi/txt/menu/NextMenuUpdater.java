package ch.rogerjaeggi.txt.menu;

import ch.rogerjaeggi.txt.R;

import com.actionbarsherlock.view.MenuItem;


public class NextMenuUpdater implements MenuUpdater {

	private final MenuItem item;
	
	public NextMenuUpdater(MenuItem item) {
		this.item = item;
	}
	
	@Override
	public void update(int page) {
		item.setEnabled(isEnabled(page));
		item.setIcon(isEnabled(page) ? R.drawable.ic_find_next_holo_dark: R.drawable.ic_find_next_holo_dark_disabled);
	}
	
	private boolean isEnabled(int page) {
		return page < 899;
	}
	
	@Override
	public MenuItem getMenuItem() {
		return item;
	}

}
