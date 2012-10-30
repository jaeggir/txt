package ch.rogerjaeggi.txt;

import ch.rogerjaeggi.utils.Logging;
import android.app.Application;


public class TxtApplication extends Application {
	
	private int currentPage = 100;
	
	private int currentPageIndex = 0;
	
	@Override
	public void onCreate() {
		super.onCreate();
		Logging.init(getApplicationContext());
	}
	
	public void setCurrentPage(int page) {
		this.currentPage = page;
	}
	
	public int getCurrentPage() {
		return currentPage;
	}

	public int getCurrentPageIndex() {
		return currentPageIndex;
	}

	public void setCurrentPageIndex(int index) {
		this.currentPageIndex = index;
	}

}
