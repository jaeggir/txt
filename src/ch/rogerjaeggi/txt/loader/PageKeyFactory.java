package ch.rogerjaeggi.txt.loader;

import static ch.rogerjaeggi.txt.Constants.DEFAULT_PAGE;
import static ch.rogerjaeggi.txt.Constants.DEFAULT_SUB_PAGE;
import ch.rogerjaeggi.txt.EChannel;


public class PageKeyFactory {
	
	public static PageKey getDefault(EChannel channel) {
		return new PageKey(channel, DEFAULT_PAGE, DEFAULT_SUB_PAGE);
	}
	
	public static PageKey getPreviousPageKey(PageInfo info) {
		return new PageKey(info.getChannel(), info.getPreviousPage(), DEFAULT_SUB_PAGE);
	}

	public static PageKey getNextPageKey(PageInfo info) {
		return new PageKey(info.getChannel(), info.getNextPage(), DEFAULT_SUB_PAGE);
	}

	public static PageKey getPageKey(PageInfo info, boolean forceRefresh) {
		return new PageKey(info.getChannel(), info.getPage(), info.getSubPage(), forceRefresh, true);
	}

	public static PageKey getPreviousSubPageKey(PageInfo info) {
		return new PageKey(info.getChannel(), info.getPage(), info.getPreviousSubPage());
	}

	public static PageKey getNextSubPageKey(PageInfo info) {
		return new PageKey(info.getChannel(), info.getPage(), info.getNextSubPage());
	}
}
