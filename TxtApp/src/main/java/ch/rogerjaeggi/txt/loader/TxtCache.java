package ch.rogerjaeggi.txt.loader;

import android.support.v4.util.LruCache;


public class TxtCache {

	private static final long CACHE_MAX_AGE = 5 * 60 * 1000l;
	private static final int CACHE_SIZE = 20;
	
	private static LruCache<PageKey, TxtResult> mMemoryCache = new LruCache<PageKey, TxtResult>(CACHE_SIZE);

	public static TxtResult put(PageKey key, TxtResult value) {
		return mMemoryCache.put(key, value);
	}
	
	public static TxtResult get(PageKey key) {
		TxtResult result = mMemoryCache.get(key);
		if (result == null) {
			if (key.getSubPage() == 0) {
				// some pages start with sub page 1, not 0
				return get(new PageKey(key.getChannel(), key.getPage(), 1));
			}
			return null;
		} else if (result.isValid(CACHE_MAX_AGE)) {
			return result;
		} else {
			mMemoryCache.remove(key);
			return null;
		}
	}
	
	public static boolean contains(PageKey key) {
		TxtResult result = mMemoryCache.get(key);
		if (result == null) {
			if (key.getSubPage() == 0) {
				// some pages start with sub page 1, not 0
				return contains(new PageKey(key.getChannel(), key.getPage(), 1));
			}
			return false;
		} else if (result.isValid(CACHE_MAX_AGE)) {
			return true;
		} else {
			mMemoryCache.remove(key);
			return false;
		}
	}

	public static void remove(PageKey key) {
		mMemoryCache.remove(key);
		if (key.getSubPage() == 0) {
			// some pages start with sub page 1, not 0
			mMemoryCache.remove(new PageKey(key.getChannel(), key.getPage(), 1));
		}
		
	}
	
}
