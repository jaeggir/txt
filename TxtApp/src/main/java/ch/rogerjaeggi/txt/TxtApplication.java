package ch.rogerjaeggi.txt;

import android.app.Application;
import android.os.Build;

import java.io.File;
import java.util.Stack;

import ch.rogerjaeggi.txt.loader.PageInfo;
import ch.rogerjaeggi.txt.loader.PageKey;
import ch.rogerjaeggi.txt.loader.RequestManager;
import ch.rogerjaeggi.txt.utils.Logging;


public class TxtApplication extends Application {
	
	private PageInfo currentPageInfo;
	
	private Stack<PageKey> history;	
	
	private RequestManager requestManager;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		Logging.init(getApplicationContext());
		enableHttpResponseCache();
		
		resetPageInfo();
		
		history = new Stack<PageKey>();
		requestManager = new RequestManager();
		requestManager.init();
	}

	public void resetPageInfo() {
		currentPageInfo = PageInfo.getDefault(Settings.getChannel(getApplicationContext()));
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
	
	public void setCurrentPageInfo(PageInfo currentPageInfo) {
		this.currentPageInfo = currentPageInfo;
	}

	public PageInfo getCurrentPageInfo() {
		return currentPageInfo;
	}

	public void pushHistory(PageKey pageKey) {
		if (history.isEmpty()) {
			history.push(pageKey);
		} else {
			PageKey top = history.peek();
			if (!top.equals(pageKey)) {
				history.push(pageKey);
			}
		}
	}
	
	public PageKey popHistory() {
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
