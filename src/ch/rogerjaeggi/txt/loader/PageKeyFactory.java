package ch.rogerjaeggi.txt.loader;

import ch.rogerjaeggi.txt.EChannel;


public class PageKeyFactory {

	private static int DEFAULT_SUB_PAGE = 0;

	public static PageKey getDefault() {
		// TODO respect channel setting
		return new PageKey(EChannel.SRF_1, 100, DEFAULT_SUB_PAGE);
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
