package ch.rogerjaeggi.txt.menu;

import com.actionbarsherlock.view.MenuItem;


public interface MenuUpdater {
	
	void update(int page);
	
	MenuItem getMenuItem();
}
