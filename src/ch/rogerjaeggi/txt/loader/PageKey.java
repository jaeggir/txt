package ch.rogerjaeggi.txt.loader;

import ch.rogerjaeggi.txt.EChannel;


public class PageKey {

	private final EChannel channel;
	
	private final int page;
	
	private final int subPage;
	
	private final boolean forceRefresh;
	
	private final boolean notifyUi;
	
	public PageKey(EChannel channel, int page, int subPage) {
		this(channel, page, subPage, false, true);
	}
	
	public PageKey(EChannel channel, int page, int subPage, boolean forceRefresh, boolean notifyUi) {
		this.channel = channel;
		this.page = page;
		this.subPage = subPage;
		this.forceRefresh = forceRefresh;
		this.notifyUi = notifyUi;
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
	
	public boolean isNotifyUi() {
		return notifyUi;
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
