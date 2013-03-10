package ch.rogerjaeggi.txt.loader;

import static ch.rogerjaeggi.txt.Constants.DEFAULT_PAGE;
import static ch.rogerjaeggi.txt.Constants.DEFAULT_SUB_PAGE;
import static ch.rogerjaeggi.txt.Constants.MAX_PAGE;
import static ch.rogerjaeggi.txt.Constants.MIN_PAGE;
import static java.lang.Integer.parseInt;
import static java.util.regex.Pattern.compile;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.rogerjaeggi.txt.EChannel;


public class PageInfo {

	private static final Pattern pattern = compile(".*\"Channel\":(-?[0-9]{1,3}).*\"PageNr\":(\\d{3}).*\"SubpageNr\":(\\d{1}).*\"PreviousPageNr\":(-?[0-9]{1,3}).*\"NextPageNr\":(-?[0-9]{1,3}).*\"PreviousSubpageNr\":(-?[0-9]{1,3}).*\"NextSubpageNr\":(-?[0-9]{1,3}).*");
	
	private final EChannel channel;
	
	private final int page;
	
	private final int subPage;
	
	private final int previousPage;
	
	private final int nextPage;
	
	private final int previousSubPage;
	
	private final int nextSubPage;
	
	private final List<TouchableArea> links;

	private PageInfo(EChannel channel, int page, int subPage, int previousPage, int nextPage, int previousSubPage, int nextSubPage) {
		this.channel = channel;
		this.page = page;
		this.subPage = subPage;
		this.previousPage = previousPage;
		this.nextPage = nextPage;
		this.previousSubPage = previousSubPage;
		this.nextSubPage = nextSubPage;
		
		this.links = new LinkedList<TouchableArea>();
	}
	
	public static PageInfo parse(String json) {
		Matcher matcher = pattern.matcher(json);
		if (matcher.find()) {
			EChannel channel = EChannel.getById(parseInt(matcher.group(1)));
			int page = parseInt(matcher.group(2));
			int subPage = parseInt(matcher.group(3));
			int previousPage = parseInt(matcher.group(4));
			int nextPage = parseInt(matcher.group(5));
			int previousSubPage = parseInt(matcher.group(6));
			int nextSubPage = parseInt(matcher.group(7));
			return new PageInfo(channel, page, subPage, previousPage, nextPage, previousSubPage, nextSubPage);
		} else {
			throw new IllegalArgumentException("Cannot parse page. Did you call matches() before?");
		}
	}
	
	public static PageInfo createFromKey(PageKey key) {
		return new PageInfo(key.getChannel(), key.getPage(), key.getSubPage(), -1, -1, -1, -1);
	}
	
	public static boolean matches(String line) {
		if (pattern.matcher(line).matches()) {
			return true;
		} else {
			return false;
		}
	}
	
	public static PageInfo getDefault(EChannel channel) {
		return createFromKey(new PageKey(channel, DEFAULT_PAGE, DEFAULT_SUB_PAGE));
	}

	public int getPage() {
		return page;
	}

	public int getSubPage() {
		return subPage;
	}

	public int getPreviousPage() {
		if (previousPage == -1) {
			return page -1;
		} else {
			return previousPage;
		}
	}

	public int getNextPage() {
		if (nextPage == -1) {
			return page + 1;
		} else {
			return nextPage;
		}
	}

	public int getPreviousSubPage() {
		return previousSubPage;
	}

	public int getNextSubPage() {
		return nextSubPage;
	}

	public EChannel getChannel() {
		return channel;
	}

	public boolean hasPreviousPage() {
		return page > MIN_PAGE;
	}
	
	public boolean hasNextPage() {
		return page < MAX_PAGE;
	}
	
	public boolean hasPreviousSubPage() {
		return previousSubPage != -1;
	}

	public boolean hasNextSubPage() {
		return nextSubPage != -1;
	}
	
	public List<TouchableArea> getLinks() {
		return links;
	}

	public void setLinks(List<TouchableArea> links) {
		this.links.addAll(links);
	}
}
