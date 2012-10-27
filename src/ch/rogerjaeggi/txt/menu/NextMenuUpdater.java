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
		boolean enabled = page < 899;
		item.setEnabled(enabled);
		item.setIcon(enabled ? R.drawable.ic_find_next_holo_dark: R.drawable.ic_find_next_holo_dark_disabled);
	}
	
	@Override
	public MenuItem getMenuItem() {
		return item;
	}

}
