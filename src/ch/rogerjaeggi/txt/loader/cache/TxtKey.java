package ch.rogerjaeggi.txt.loader.cache;

import ch.rogerjaeggi.txt.EChannel;


public class TxtKey {

	private final int channelId;
	private final int page;
	private final int subPage;
	
	public TxtKey(EChannel channel, int page, int subPage) {
		this.channelId = channel.getId();
		this.page = page;
		this.subPage = subPage;
	}
	
	@Override
	public int hashCode() {
		int hc = 17; 
		int hashMultiplier = 59; 
		return hc + channelId * hashMultiplier + page * hashMultiplier + subPage * hashMultiplier;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		} else if (o instanceof TxtKey) {
			TxtKey other = (TxtKey) o;
			return channelId == other.channelId && page == other.page && subPage == other.subPage; 
		} else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return "TxtKey[channel=" + channelId + ", page=" + page + ", subPage=" + subPage + "]";
	}
	
}
