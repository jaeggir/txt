package ch.rogerjaeggi.txt.menu;

import ch.rogerjaeggi.txt.loader.PageInfo;

import com.actionbarsherlock.view.MenuItem;


public interface MenuUpdater {
	
	void update(PageInfo pageInfo);
	
	MenuItem getMenuItem();
}
