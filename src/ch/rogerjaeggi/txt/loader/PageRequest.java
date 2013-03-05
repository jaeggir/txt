package ch.rogerjaeggi.txt.loader;

import ch.rogerjaeggi.txt.loader.cache.TxtKey;


public class PageRequest {

	private final TxtKey key;
	
	private boolean loadPageLinks;
	
	private boolean forceRefresh;
	
	public PageRequest(TxtKey key, boolean loadPageLinks, boolean forceRefresh) {
		this.key = key;
		this.loadPageLinks = loadPageLinks;
		this.forceRefresh = forceRefresh;
	}	
	
	public TxtKey getKey() {
		return key;
	}
	
	public boolean isLoadPageLinks() {
		return loadPageLinks;
	}
	
	public boolean isForceRefresh() {
		return forceRefresh;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof PageRequest)) {
			return false;
		} else {
			PageRequest other = (PageRequest) o;
			return key.equals(other.key);
		}
	}
	
	@Override
	public int hashCode() {
		return key.hashCode();
	}
	
	@Override
	public String toString() {
		return key.toString() + ", forceRefresh=" + forceRefresh + ", loadPageLinks=" + loadPageLinks;
	}
}
