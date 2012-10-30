package ch.rogerjaeggi.txt.loader;

import ch.rogerjaeggi.utils.tasks.ITaskActivityCallable;


public interface IPageActivityCallable extends ITaskActivityCallable<TxtResult> {

	void updateMenuItems();
	
	void cancelRefreshIndicators();
	
	void startRefreshIndicators();
	
}
