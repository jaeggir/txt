package ch.rogerjaeggi.txt.loader;

import static ch.rogerjaeggi.txt.loader.PageKey.DEFAULT_PAGE;
import static ch.rogerjaeggi.txt.loader.PageKey.DEFAULT_SUB_PAGE;
import ch.rogerjaeggi.txt.EChannel;


public class PageKeyFactory {
	
	public static PageKey getDefault(EChannel channel) {
		return new PageKey(channel, DEFAULT_PAGE, DEFAULT_SUB_PAGE);
	}
	
	public static PageKey fromKey(PageKey key, String newSubPage) {
		try {
			return new PageKey(key.getChannel(), key.getPage(), Integer.parseInt(newSubPage));
		} catch (NumberFormatException e) {
			// TODO handle error and think about correct location for this method
			throw new IllegalArgumentException();
		}
	}
	
	public static PageKey getPreviousPageKey(PageInfo info) {
		return new PageKey(info.getChannel(), info.getPreviousPage(), DEFAULT_SUB_PAGE);
	}

	public static PageKey getNextPageKey(PageInfo info) {
		return new PageKey(info.getChannel(), info.getNextPage(), DEFAULT_SUB_PAGE);
	}

	public static PageKey getPageKey(PageInfo info, boolean forceRefresh) {
		return new PageKey(info.getChannel(), info.getPage(), info.getSubPage(), forceRefresh);
	}

	public static PageKey getPreviousSubPageKey(PageInfo info) {
		return new PageKey(info.getChannel(), info.getPage(), info.getPreviousSubPage());
	}

	public static PageKey getNextSubPageKey(PageInfo info) {
		return new PageKey(info.getChannel(), info.getPage(), info.getNextSubPage());
	}
}
