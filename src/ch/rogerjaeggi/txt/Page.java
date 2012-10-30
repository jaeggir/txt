package ch.rogerjaeggi.txt;


public class Page {

	private final EChannel channel;
	
	private final int page;
	
	private final int subPage;
	
	public Page(EChannel channel, int page, int subPage) {
		this.channel = channel;
		this.page = page;
		this.subPage = subPage;
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
	
	@Override
	public int hashCode() {
		int hc = 17; 
		int hashMultiplier = 59; 
		return hc + channel.hashCode() * hashMultiplier + page * hashMultiplier + subPage * hashMultiplier;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		} else if (o instanceof Page) {
			Page other = (Page) o;
			return channel == other.channel && page == other.page && subPage == other.subPage; 
		} else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return "Page[channel=" + channel + ", page=" + page + ", subPage=" + subPage + "]";
	}
	
}
