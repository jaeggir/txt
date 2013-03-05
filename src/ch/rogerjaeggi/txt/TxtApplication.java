package ch.rogerjaeggi.txt;

import java.io.File;
import java.util.Stack;

import android.app.Application;
import android.os.Build;
import ch.rogerjaeggi.txt.loader.RequestManager;
import ch.rogerjaeggi.utils.Logging;


public class TxtApplication extends Application {
	
	private int currentPage = 100;
	
	private int currentSubPage = 0;
	
	private Stack<Page> history;	
	
	private RequestManager requestManager;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		Logging.init(getApplicationContext());
		enableHttpResponseCache();
		
		
		history = new Stack<Page>();
		requestManager = new RequestManager();
		requestManager.init();
	}
	
	private void enableHttpResponseCache() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		    try {
		        long httpCacheSize = 1 * 1024 * 1024; // 1 MiB
		        File httpCacheDir = new File(getApplicationContext().getCacheDir(), "http");
		        Class.forName("android.net.http.HttpResponseCache").getMethod("install", File.class, long.class).invoke(null, httpCacheDir, httpCacheSize);
		    } catch (Exception httpResponseCacheNotAvailable) {
		    	// ignore
		    }
		}
	}
	
	public void setCurrentPage(int page) {
		this.currentPage = page;
	}
	
	public int getCurrentPage() {
		return currentPage;
	}

	public int getCurrentSubPage() {
		return currentSubPage;
	}

	public void setCurrentSubPage(int subPage) {
		this.currentSubPage = subPage;
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
	
	public RequestManager getRequestManager() {
		return requestManager;
	}

}
