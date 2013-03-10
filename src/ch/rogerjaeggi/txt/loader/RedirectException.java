package ch.rogerjaeggi.txt.loader;


public class RedirectException extends Exception {

	private final PageKey pageKey;
	
	public RedirectException(PageKey pageKey) {
		this.pageKey = pageKey;
	}
	
	public PageKey getPageKey() {
		return pageKey;
	}
	
}
