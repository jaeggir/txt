package ch.rogerjaeggi.txt.loader;

import static ch.rogerjaeggi.txt.Constants.TXT_BASE_URL;
import static ch.rogerjaeggi.txt.loader.PageInfo.createFromKey;
import static java.lang.Integer.parseInt;
import static java.lang.Math.max;
import static java.util.regex.Pattern.compile;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import ch.rogerjaeggi.txt.EChannel;

public abstract class LoadPageTask {

	private static final Pattern redirectPattern = Pattern.compile("/(.*)/(\\d{3})-(\\d{2}).html");

	private static final Pattern areaPattern = compile("<area href=\"/\\w+/\\d{3}-(\\d{2}).html\".*coords=\"(\\d+),(\\d+),(\\d+),(\\d+)\" alt=\"(\\d{3})\" />");

	protected static final String TAG = "txt.pageActivity";

	protected static final int IO_BUFFER_SIZE = 4 * 1024;

	private PageKey key;

	public LoadPageTask(PageKey key) {
		this.key = key;
	}

	public PageKey getKey() {
		return key;
	}

	public boolean isForceRefresh() {
		return key.isForceRefresh();
	}

	public TxtResult execute() throws PageNotFoundException, CannotParseImageException, URISyntaxException, IOException {
		if (!isForceRefresh() && TxtCache.contains(key)) {
			return TxtCache.get(key);
		} else {
			try {
				PageInfo pageInfo = loadPageInfo(getPageUrl());
				try {
					Bitmap bitmap = loadImage(getImageUrl());
					TxtResult result = new TxtResult(pageInfo, bitmap);
					TxtCache.put(key, result);
					return result;
				} catch (FileNotFoundException e) {
					throw new PageNotFoundException(pageInfo);
				}
			} catch (RedirectException e) {
				this.key = e.getPageKey();
				return execute();
			}
		}
	}

	private String getImageUrl() {
		return TXT_BASE_URL + "dynpics/" + key.getChannel().getUrl() + "/" + key.getPage() + "-0" + key.getSubPage() + ".gif";
	}

	private String getPageUrl() {
		return TXT_BASE_URL + key.getChannel().getUrl() + "/" + key.getPage() + "-0" + key.getSubPage() + ".html";
	}

	protected abstract PageInfo loadPageInfo(String url) throws RedirectException, URISyntaxException, IOException;

	protected abstract Bitmap loadImage(String url) throws FileNotFoundException, URISyntaxException, CannotParseImageException, IOException;

	protected Bitmap decodeBitmap(ByteArrayOutputStream dataStream) throws CannotParseImageException {
		final byte[] imgData = dataStream.toByteArray();
		BitmapFactory.Options options = new BitmapFactory.Options();
		Bitmap bitmap = BitmapFactory.decodeByteArray(imgData, 0, imgData.length, options);
		if (bitmap == null) {
			throw new CannotParseImageException("Couldn't decode image");
		} else {
			return bitmap;
		}
	}
	
	protected PageInfo parsePage(BufferedReader br) throws IOException {
		PageInfo pageInfo = createFromKey(key);
		List<TouchableArea> links = new ArrayList<TouchableArea>();
		String line = null;
		boolean start = false;
		while ((line = br.readLine()) != null) {
			if (start &&  areaPattern.matcher(line).matches()) {
				links.add(getAreaFromLine(line));
			}
			if (line.contains("map") && line.contains("blacktxt_links_" + max(0, key.getSubPage() - 1))) {
				start = true;
			}
			if (start && line.contains("</map>")) {
				start = false;
			}
			if (PageInfo.matches(line)) {
				pageInfo = PageInfo.parse(line.trim());
			}
		}
		pageInfo.setLinks(links);
		return pageInfo;
	}

	private TouchableArea getAreaFromLine(String line) {
		Matcher matcher = areaPattern.matcher(line);
		if (matcher.find()) {
			int page = parseInt(matcher.group(6));
			int subPage = parseInt(matcher.group(1));
			Rect area = new Rect(parseInt(matcher.group(2)) - 2, parseInt(matcher.group(3)) - 2, parseInt(matcher.group(4)) + 2, parseInt(matcher.group(5)) + 2);
			return new TouchableArea(area, page, subPage);
		} else {
			throw new IllegalArgumentException("Cannot parse area. Did you call matches() before?");
		}
	}

	protected IOException handleRedirect(String path) {
		Matcher matcher = redirectPattern.matcher(path);
		if (matcher.find()) {
			EChannel channel = EChannel.getByUrl(matcher.group(1));
			int page = parseInt(matcher.group(2));
			int subPage = parseInt(matcher.group(3));
			PageKey pageKey = new PageKey(channel, page, subPage, getKey().isForceRefresh());
			return new RedirectException(pageKey);
		} else {
			return new IOException("Redirect to malformed redirect - URL detected. URL=" + path);
		}
	}

}
