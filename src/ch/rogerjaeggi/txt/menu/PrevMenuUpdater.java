package ch.rogerjaeggi.txt.menu;

import ch.rogerjaeggi.txt.R;

import com.actionbarsherlock.view.MenuItem;


public class PrevMenuUpdater implements MenuUpdater {

	private final MenuItem item;
	
	public PrevMenuUpdater(MenuItem item) {
		this.item = item;
	}
	
	@Override
	public void update(int page) {
		item.setEnabled(isEnabled(page));
		item.setIcon(isEnabled(page) ? R.drawable.ic_find_previous_holo_dark: R.drawable.ic_find_previous_holo_dark_disabled);
	}
	
	private boolean isEnabled(int page) {
		return page > 100;
	}
	
	@Override
	public MenuItem getMenuItem() {
		return item;
	}

}
