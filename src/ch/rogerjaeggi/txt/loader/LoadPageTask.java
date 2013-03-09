package ch.rogerjaeggi.txt.loader;

import static ch.rogerjaeggi.txt.Constants.TXT_BASE_URL;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Rect;

public abstract class LoadPageTask {

	protected static final String TAG = "txt.pageActivity";

	protected static final int IO_BUFFER_SIZE = 4 * 1024;
	
	private PageKey key;
	
	public LoadPageTask(PageKey key) {
		this.key = key;
	}
	
	public PageKey getKey() { 
		return key;
	}
	
	protected void updateSubPage(String subPage) {
		this.key = PageKeyFactory.fromKey(key, subPage);
	}
	
	public boolean isForceRefresh() {
		return key.isForceRefresh();
	}
	
	public TxtResult execute() throws FileNotFoundException, IOException {
		if (!isForceRefresh() && TxtCache.contains(key)) {
			return TxtCache.get(key);
		} else {
			
			PageInfo pageInfo = loadPageInfo(getPageUrl());
			TxtResult result = null;
			if (TxtCache.contains(key)) {
				// TODO loadPageInfo can update the key (redirects). Clean-up this mess.
				return TxtCache.get(key);
			} else  {
				Bitmap bitmap = loadImage(getImageUrl());
				result = new TxtResult(pageInfo, bitmap);
				TxtCache.put(key, result);				
				return result;
			}
		}
	}

	private String getImageUrl() {
		return TXT_BASE_URL + "dynpics/" + key.getChannel().getUrl() + "/" + key.getPage() + "-0" + key.getSubPage() + ".gif";
	}
	
	private String getPageUrl() {
		return TXT_BASE_URL  + key.getChannel().getUrl() + "/" + key.getPage() + "-0" + key.getSubPage() + ".html";
	}

	protected abstract PageInfo loadPageInfo(String url) throws IOException;
	
	protected abstract Bitmap loadImage(String url) throws FileNotFoundException, IOException;
	
	protected PageInfo parsePage(BufferedReader br) throws IOException {
		
		String page = "";
		
		PageInfo pageInfo = null;
		List<TouchableArea> links = new ArrayList<TouchableArea>();
	    String s;
	    boolean start = false;
	    while ((s = br.readLine()) != null) {
	    	page = page + "\n" + s;
	    	if (start && s.contains("<area")) {
	    		TouchableArea area = getAreaFromLine(s);
	    		if (area != null) {
	    			links.add(area);
	    		}
	    	}
	    	// TODO whats that? why subpage -1 ?
	    	if (s.contains("map") && s.contains("blacktxt_links_" + (key.getSubPage() == 0 ? 0 : key.getSubPage() - 1))) {
	    		start = true;
	    	}
	    	if (start && s.contains("</map>")) {
	    		start = false;
	    	}
	    	if (s.trim().startsWith("Txt.txtPage = {")) {
	    		pageInfo = PageInfo.parse(s.trim());
	    	}
	    }
	    
	    if (pageInfo == null) {
	    	// TODO handle case if pageInfo is null and remove system out.
	    	System.out.println(page);
	    	throw new IOException("parsing page failed. No PageInfo found.");
	    }
    	pageInfo.setLinks(links);
	    return pageInfo;
	}
	
	// TODO parse subPage (from link)
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
