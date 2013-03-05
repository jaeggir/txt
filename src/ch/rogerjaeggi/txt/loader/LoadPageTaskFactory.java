package ch.rogerjaeggi.txt.loader;

import android.os.Build;


public class LoadPageTaskFactory {

	public static LoadPageTask createTask(PageRequest request) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			return new HttpClientTask(request);
		} else {
			return new UrlConnectionTask(request);
		}
	}
	
}
