package ch.rogerjaeggi.txt.loader;

import java.io.IOException;


public class RedirectException extends IOException {

	private final PageKey pageKey;
	
	public RedirectException(PageKey pageKey) {
		this.pageKey = pageKey;
	}
	
	public PageKey getPageKey() {
		return pageKey;
	}
	
}
