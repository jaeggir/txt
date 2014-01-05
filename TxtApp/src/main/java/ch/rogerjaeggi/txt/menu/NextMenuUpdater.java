package ch.rogerjaeggi.txt.menu;

import android.view.MenuItem;

import ch.rogerjaeggi.txt.R;
import ch.rogerjaeggi.txt.loader.PageInfo;


public class NextMenuUpdater implements MenuUpdater {

	private final MenuItem item;
	
	public NextMenuUpdater(MenuItem item) {
		this.item = item;
	}
	
	@Override
	public void update(PageInfo pageInfo) {
		boolean hasNextPage = pageInfo.hasNextPage();
		item.setEnabled(hasNextPage);
		item.setIcon(hasNextPage ? R.drawable.ic_find_next_holo_dark: R.drawable.ic_find_next_holo_dark_disabled);
	}
	
	@Override
	public MenuItem getMenuItem() {
		return item;
	}

}
