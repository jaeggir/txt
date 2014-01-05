package ch.rogerjaeggi.txt.menu;

import android.view.MenuItem;

import ch.rogerjaeggi.txt.loader.PageInfo;


public interface MenuUpdater {
	
	void update(PageInfo pageInfo);
	
	MenuItem getMenuItem();
}
