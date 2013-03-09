package ch.rogerjaeggi.txt.loader;

import static ch.rogerjaeggi.txt.Constants.TXT_BASE_URL;
import static java.lang.Integer.parseInt;
import static java.lang.Math.max;
import static java.util.regex.Pattern.compile;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.graphics.Bitmap;
import android.graphics.Rect;

public abstract class LoadPageTask {

	private static Pattern pattern = compile("<area href=\"/\\w+/\\d{3}-(\\d{2}).html\" shape=\"rect\" coords=\"(\\d+),(\\d+),(\\d+),(\\d+)\" alt=\"(\\d{3})\" />");

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

	public TxtResult execute() throws PageNotFoundException, IOException {
		if (!isForceRefresh() && TxtCache.contains(key)) {
			return TxtCache.get(key);
		} else {

			PageInfo pageInfo = loadPageInfo(getPageUrl());
			TxtResult result = null;
			if (TxtCache.contains(key)) {
				// TODO loadPageInfo can update the key (redirects). Clean-up this mess.
				return TxtCache.get(key);
			} else {
				try {
					Bitmap bitmap = loadImage(getImageUrl());
					result = new TxtResult(pageInfo, bitmap);
					TxtCache.put(key, result);
					return result;
				} catch (FileNotFoundException e) {
					throw new PageNotFoundException(pageInfo);
				}
			}
		}
	}

	private String getImageUrl() {
		return TXT_BASE_URL + "dynpics/" + key.getChannel().getUrl() + "/" + key.getPage() + "-0" + key.getSubPage() + ".gif";
	}

	private String getPageUrl() {
		return TXT_BASE_URL + key.getChannel().getUrl() + "/" + key.getPage() + "-0" + key.getSubPage() + ".html";
	}

	protected abstract PageInfo loadPageInfo(String url) throws IOException;

	protected abstract Bitmap loadImage(String url) throws FileNotFoundException, IOException;

	protected PageInfo parsePage(BufferedReader br) throws IOException {

		PageInfo pageInfo = null;
		List<TouchableArea> links = new ArrayList<TouchableArea>();
		String s;
		boolean start = false;
		while ((s = br.readLine()) != null) {
			if (start && s.contains("<area")) {
				TouchableArea area = getAreaFromLine(s.trim());
				if (area != null) {
					links.add(area);
				}
			}
			if (s.contains("map") && s.contains("blacktxt_links_" + max(0, key.getSubPage() - 1))) {
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
			// TODO handle case if pageInfo is null
			throw new IOException("parsing page failed. No PageInfo found.");
		}
		pageInfo.setLinks(links);
		return pageInfo;
	}

	private TouchableArea getAreaFromLine(String line) {
		Matcher matcher = pattern.matcher(line);
		if (matcher.find()) {
			int page = parseInt(matcher.group(6));
			int subPage = parseInt(matcher.group(1));
			Rect area = new Rect(parseInt(matcher.group(2)) - 2, parseInt(matcher.group(3)) - 2, parseInt(matcher.group(4)) + 2, parseInt(matcher.group(5)) + 2);
			return new TouchableArea(area, page, subPage);
		} else {
			return null;
		}
	}

}
