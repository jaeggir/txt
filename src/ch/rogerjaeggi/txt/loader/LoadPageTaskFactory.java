package ch.rogerjaeggi.txt.loader;

import android.content.Context;
import android.os.Build;
import ch.rogerjaeggi.txt.EChannel;


public class LoadPageTaskFactory {

	public static LoadPageTask createTask(Context context, EChannel channel, int page, int subPage, boolean loadPageLinks, boolean forceRefresh) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			return new HttpClientTask(channel, page, subPage, loadPageLinks, forceRefresh);
		} else {
			return new UrlConnectionTask(context, channel, page, subPage, loadPageLinks, forceRefresh);
		}
	}
	
}
