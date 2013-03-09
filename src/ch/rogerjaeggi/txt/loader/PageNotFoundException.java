package ch.rogerjaeggi.txt.loader;


public class PageNotFoundException extends Exception {

	private final PageInfo pageInfo;
	
	public PageNotFoundException(PageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}
	
	public PageInfo getPageInfo() {
		return pageInfo;
	}

}
