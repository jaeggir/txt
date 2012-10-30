package ch.rogerjaeggi.txt;

import java.util.Stack;

import android.app.Application;
import ch.rogerjaeggi.utils.Logging;


public class TxtApplication extends Application {
	
	private int currentPage = 100;
	
	private int currentPageIndex = 0;
	
	private Stack<Page> history;
	
	@Override
	public void onCreate() {
		super.onCreate();
		Logging.init(getApplicationContext());
		history = new Stack<Page>();
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
	
	public void pushHistory(EChannel channel, int page, int subPage) {
		Page newPage = new Page(channel, page, subPage);
		if (history.isEmpty()) {
			history.push(newPage);
		} else {
			Page top = history.peek();
			if (!top.equals(newPage)) {
				history.push(newPage);
			}
		}
	}
	
	public Page popHistory() {
		if (history.isEmpty()) {
			return null;
		} else {
			return history.pop();
		}
	}

}
