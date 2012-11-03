package ch.rogerjaeggi.txt.loader.cache;

import android.support.v4.util.LruCache;
import ch.rogerjaeggi.txt.loader.TxtResult;


public class TxtCache {

	private static final int CACHE_SIZE = 20;
	
	private static LruCache<TxtKey, TxtResult> mMemoryCache = new LruCache<TxtKey, TxtResult>(CACHE_SIZE);

	public static TxtResult put(TxtKey key, TxtResult value) {
		return mMemoryCache.put(key, value);
	}
	
	public static TxtResult get(TxtKey key) {
		TxtResult result = mMemoryCache.get(key);
		if (result == null && key.getSubPage() == 0) {
			key = new TxtKey(key.getChannel(), key.getPage(), key.getSubPage() + 1);
			result = mMemoryCache.get(key);
		}
		if (result == null || result.isValid()) {
			return result;
		} else {
			mMemoryCache.remove(key);
			return null;
		}
	}
	
	public static boolean contains(TxtKey key) {
		TxtResult result = mMemoryCache.get(key);
		if (result == null && key.getSubPage() == 0) {
			key = new TxtKey(key.getChannel(), key.getPage(), key.getSubPage() + 1);
			result = mMemoryCache.get(key);
		}
		if (result == null) {
			return false;
		} else if (result.isValid()) {
			return true;
		} else {
			mMemoryCache.remove(key);
			return false;
		}
	}
	
}
