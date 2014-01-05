package ch.rogerjaeggi.txt.menu;

import ch.rogerjaeggi.txt.R;
import ch.rogerjaeggi.txt.loader.PageInfo;

import com.actionbarsherlock.view.MenuItem;


public class PrevMenuUpdater implements MenuUpdater {

	private final MenuItem item;
	
	public PrevMenuUpdater(MenuItem item) {
		this.item = item;
	}
	
	@Override
	public void update(PageInfo pageInfo) {
		boolean hasPreviousPage = pageInfo.hasPreviousPage();
		item.setEnabled(hasPreviousPage);
		item.setIcon(hasPreviousPage ? R.drawable.ic_find_previous_holo_dark: R.drawable.ic_find_previous_holo_dark_disabled);
	}
	
	@Override
	public MenuItem getMenuItem() {
		return item;
	}

}
