package ch.rogerjaeggi.txt.loader;

import ch.rogerjaeggi.txt.EChannel;


public class PageKey {

	public static final int DEFAULT_PAGE = 100;

	public static final int DEFAULT_SUB_PAGE = 0;

	public static final int MIN_PAGE = 100;
	
	public static final int MAX_PAGE = 999;

	private final EChannel channel;
	
	private final int page;
	
	private final int subPage;
	
	private final boolean forceRefresh;
	
	public PageKey(EChannel channel, int page, int subPage) {
		this(channel, page, subPage, false);
	}
	
	public PageKey(EChannel channel, int page, int subPage, boolean forceRefresh) {
		this.channel = channel;
		this.page = page;
		this.subPage = subPage;
		this.forceRefresh = forceRefresh;
	}
	
	public EChannel getChannel() {
		return channel;
	}
	
	public int getPage() {
		return page;
	}
	
	public int getSubPage() {
		return subPage;
	}
	
	public boolean isForceRefresh() {
		return forceRefresh;
	}
	
	@Override
	public int hashCode() {
		int hc = 17; 
		int hashMultiplier = 59; 
		return hc + channel.getId() * hashMultiplier + page * hashMultiplier + subPage * hashMultiplier;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof PageKey)) {
			return false;
		} else {
			PageKey other = (PageKey) o;
			return channel == other.channel && page == other.page && subPage == other.subPage;
		}
	}
	
	@Override
	public String toString() {
		return "PageKey[channel=" + channel + ", page=" + page + ", subPage=" + subPage + ", forceRefresh=" + forceRefresh + "]";
	}
	
}
