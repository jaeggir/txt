package ch.rogerjaeggi.txt.loader;

import static ch.rogerjaeggi.txt.loader.PageKey.DEFAULT_PAGE;
import static ch.rogerjaeggi.txt.loader.PageKey.DEFAULT_SUB_PAGE;
import static ch.rogerjaeggi.txt.loader.PageKey.MAX_PAGE;
import static ch.rogerjaeggi.txt.loader.PageKey.MIN_PAGE;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ch.rogerjaeggi.txt.EChannel;


public class PageInfo {

	private final Map<String, String> properties;
	
	private final List<TouchableArea> links;

	public static PageInfo parse(String json) {
		int startIndex = json.indexOf("{");
		int endIndex = json.lastIndexOf("}");

		Map<String, String> props = new HashMap<String, String>();

		if (startIndex != -1 && endIndex != -1) {
			json = json.substring(startIndex + 1, endIndex);
			json = json.replace("\"", "");
			String[] values = json.split(",");
			for (String value : values) {
				String[] tmp = value.split(":");
				if (tmp.length == 2) {
					props.put(tmp[0], tmp[1]);
				}
			}
			return new PageInfo(props);
		} else {
			throw new IllegalArgumentException("Cannot parse '" + json + "'");
		}
	}
	
	public static PageInfo createFromKey(PageKey key) {
		Map<String, String> props = new HashMap<String, String>();
	
		props.put("Channel", Integer.toString(key.getChannel().getId()));
		props.put("PageNr", Integer.toString(key.getPage()));
		props.put("SubpageNr", Integer.toString(key.getSubPage()));
		
		return new PageInfo(props);
	}
	
	public static PageInfo getDefault(EChannel channel) {
		String json = "Txt.txtPage = {\"Id\":\"" + channel.getName() + "_100_01\",\"Channel\":" + channel.getId() + ",\"PageNr\":100,\"SubpageNr\":1,\"PageUrl\":\"~/SRF1/100-01.html\",\"NumberOfSubpages\":1,\"PreviousPageNr\":-1,\"NextPageNr\":101,\"PreviousSubpageNr\":-1,\"NextSubpageNr\":-1};";	
		return parse(json);
	}

	private PageInfo(Map<String, String> properties) {
		this.properties = properties;
		this.links = new LinkedList<TouchableArea>();
	}

	public int getPage() {
		return getIntProperty("PageNr", DEFAULT_PAGE);
	}

	public int getSubPage() {
		return getIntProperty("SubpageNr", DEFAULT_SUB_PAGE);
	}

	public int getPreviousPage() {
		int page = getIntProperty("PreviousPageNr", DEFAULT_PAGE);
		if (page == -1) {
			return getPage() -1;
		} else {
			return page;
		}
	}

	public int getNextPage() {
		int page = getIntProperty("NextPageNr", DEFAULT_PAGE);
		if (page == -1) {
			return getPage() + 1;
		} else {
			return page;
		}
	}

	public int getPreviousSubPage() {
		return getIntProperty("PreviousSubpageNr", -1);
	}

	public int getNextSubPage() {
		return getIntProperty("NextSubpageNr", -1);
	}

	public EChannel getChannel() {
		int channel = getIntProperty("Channel", 1);
		return EChannel.getById(channel);
	}

	public boolean hasPreviousPage() {
		return getPage() > MIN_PAGE;
	}
	
	public boolean hasNextPage() {
		return getPage() < MAX_PAGE;
	}
	
	public boolean hasPreviousSubPage() {
		return getPreviousSubPage() != -1;
	}

	public boolean hasNextSubPage() {
		return getNextSubPage() != -1;
	}
	
	public List<TouchableArea> getLinks() {
		return links;
	}

	public void setLinks(List<TouchableArea> links) {
		this.links.addAll(links);
	}

	private String getProperty(String key) {
		return properties.get(key);
	}

	private int getIntProperty(String key, int defaultValue) {
		String property = getProperty(key);
		if (property != null) {
			try {
				return Integer.parseInt(property);
			} catch (NumberFormatException e) {
			}
		}
		return defaultValue;
	}
}
