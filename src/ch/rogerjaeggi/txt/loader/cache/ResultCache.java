package ch.rogerjaeggi.txt.loader.cache;

import android.support.v4.util.LruCache;
import ch.rogerjaeggi.txt.EChannel;
import ch.rogerjaeggi.txt.loader.TxtResult;


public class ResultCache {

	private static LruCache<TxtKey, TxtResult> mMemoryCache = new LruCache<TxtKey, TxtResult>(50);
		
	public static TxtResult getResult(EChannel channel, int page, int subPage) {
		TxtResult result = mMemoryCache.get(new TxtKey(channel, page, subPage));
		if (result == null && subPage == 0) {
			return mMemoryCache.get(new TxtKey(channel, page, subPage + 1));
		} else {
			return result;
		}
	}
	
	public static void storeResult(EChannel channel, int page, int subPage, TxtResult result) {
		TxtKey key = new TxtKey(channel, page, subPage);
		mMemoryCache.put(key, result);
	}
	
}
