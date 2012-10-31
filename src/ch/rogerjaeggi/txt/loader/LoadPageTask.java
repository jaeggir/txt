package ch.rogerjaeggi.txt.loader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Rect;
import ch.rogerjaeggi.txt.EChannel;
import ch.rogerjaeggi.txt.loader.cache.InMemoryResultCache;
import ch.rogerjaeggi.utils.tasks.BetterTask;

public abstract class LoadPageTask extends BetterTask<Void, Void, TxtResult> {

	protected static final int IO_BUFFER_SIZE = 4 * 1024;

	protected static final String TAG = "txt.pageActivity";

	protected static final String BASE_URL = "http://www.teletext.ch/";
	
	protected final EChannel channel;
	protected final int page;
	protected int subPage;
	protected final boolean loadPageLinks;
	protected final boolean forceRefresh;

	private FileNotFoundException error;
	
	public LoadPageTask(EChannel channel, int page, int subPage, boolean loadPageLinks, boolean forceRefresh) {
		this.channel = channel;
		this.page = page;
		this.subPage = subPage;
		this.loadPageLinks = loadPageLinks;
		this.forceRefresh = forceRefresh;
	}
	
	public int getSubPage() { 
		return subPage;
	}

	public int getPage() {
		return page;
	}
	
	public boolean doesPageExists() {
		return error == null;
	}
	
	@Override
	protected TxtResult doInBackground(Void... params) {
		try {
			TxtResult cachedResult = null;
			if (!forceRefresh) {
				cachedResult = InMemoryResultCache.getResult(channel, page, subPage);
			}
			if (cachedResult != null) {
				return cachedResult;
			} else {
				TxtResult result = doWork();
				InMemoryResultCache.storeResult(channel, page, subPage, result);
				return result;
			}
		} catch (FileNotFoundException e) {
			error = e;
			return new TxtResult(null);
		} catch (IOException e) {
			return new TxtResult(null);
		}
	}
	
	private IPageActivityCallable getPageCallable() {
		if (getCallable() instanceof IPageActivityCallable) {
			return (IPageActivityCallable) getCallable();
		} else {
			return null;
		}
	}

	@Override
	protected void onPreExecute() {
		if (!canLoadFromCache()) {
			if (getPageCallable() != null) {
				getPageCallable().startRefreshIndicators();
			}
			super.onPreExecute();
		}
	}
	
	@Override
	protected void onCancelled() {
		if (getPageCallable() != null) {
			getPageCallable().cancelRefreshIndicators();
			getPageCallable().updateMenuItems();
		}
		super.onCancelled();
	}
	
	@Override
	protected void onPostExecute(TxtResult result) {
		if (getPageCallable() != null) {
			getPageCallable().cancelRefreshIndicators();
		}
		super.onPostExecute(result);
	}
	
	private boolean canLoadFromCache() {
		return !forceRefresh && InMemoryResultCache.contains(channel, page, subPage);
	}
	
	protected abstract TxtResult doWork() throws FileNotFoundException, IOException;
	
	protected abstract List<TouchableArea> findTouchableAreas();
	
	protected List<TouchableArea> parsePage(BufferedReader br) throws IOException {
		List<TouchableArea> areas = new ArrayList<TouchableArea>();
	    String s;
	    boolean start = false;
	    while ((s = br.readLine()) != null) {
	    	if (start && s.contains("<area")) {
	    		TouchableArea area = getAreaFromLine(s);
	    		if (area != null) {
	    			areas.add(area);
	    		}
	    	}
	    	if (s.contains("map") && s.contains("blacktxt_links_" + (subPage == 0 ? 0 : subPage - 1))) {
	    		start = true;
	    	}
	    	if (start && s.contains("</map>")) {
	    		start = false;
	    		break;
	    	}
	    }
	    return areas;
	}
	
	private TouchableArea getAreaFromLine(String s) {
		s = s.trim();
		int start = s.indexOf("coords=\"");
		if (start != -1) {
			s = s.substring(start + "coords=\"".length());
			int end = s.indexOf("\"");
			String coords = s.substring(0, end);
			if (coords.split(",").length == 4) {
				String[] values = coords.split(",");
				start = s.indexOf("alt=\"");
				if (start != -1) {
					s = s.substring(start + "alt=\"".length());
					end = s.indexOf("\"");
					String target = s.substring(0, end);
					try {
						Rect area = new Rect(Integer.parseInt(values[0]), Integer.parseInt(values[1]), Integer.parseInt(values[2]), Integer.parseInt(values[3]));
						int pageTarget = Integer.parseInt(target);
						return new TouchableArea(area, pageTarget);
					} catch (NumberFormatException e) {
						// ignore
					}
				}
				
			}
		}
		return null;
	}
	
}
