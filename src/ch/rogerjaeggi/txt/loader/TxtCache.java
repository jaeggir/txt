package ch.rogerjaeggi.txt.loader;

import android.support.v4.util.LruCache;


public class TxtCache {
	
	private static final long MAX_AGE = 5 * 60 * 1000l;

	private static final int CACHE_SIZE = 20;
	
	private static LruCache<PageKey, TxtResult> mMemoryCache = new LruCache<PageKey, TxtResult>(CACHE_SIZE);

	public static TxtResult put(PageKey key, TxtResult value) {
		return mMemoryCache.put(key, value);
	}
	
	public static TxtResult get(PageKey key) {
		TxtResult result = mMemoryCache.get(key);
		if (result == null || result.isValid(MAX_AGE)) {
			return result;
		} else {
			mMemoryCache.remove(key);
			return null;
		}
	}
	
	public static boolean contains(PageKey key) {
		TxtResult result = mMemoryCache.get(key);
		if (result == null) {
			return false;
		} else if (result.isValid(MAX_AGE)) {
			return true;
		} else {
			mMemoryCache.remove(key);
			return false;
		}
	}
	
}