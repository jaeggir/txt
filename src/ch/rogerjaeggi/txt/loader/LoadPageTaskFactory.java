package ch.rogerjaeggi.txt.loader;

import android.os.Build;


public class LoadPageTaskFactory {

	public static LoadPageTask createTask(PageKey key) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			return new HttpClientTask(key);
		} else {
			return new UrlConnectionTask(key);
		}
	}
	
}
