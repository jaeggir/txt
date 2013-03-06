package ch.rogerjaeggi.txt.loader;

// TODO get rid of by adding forceRefresh to PageKey ?
public class PageRequest {

	private final PageKey key;
		
	private boolean forceRefresh;
	
	public PageRequest(PageKey key, boolean forceRefresh) {
		this.key = key;
		this.forceRefresh = forceRefresh;
	}	
	
	public PageKey getKey() {
		return key;
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
		return key.toString() + ", forceRefresh=" + forceRefresh;
	}
}
