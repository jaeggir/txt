package ch.rogerjaeggi.txt;


public enum EChannel {
	
	SRF_1(1, "SRF1", "SRF 1"), 
	SRF_ZWEI(2, "SRFzwei", "SRF zwei"), 
	SRF_INFO(3, "SRFinfo", "SRF Info"), 
	RTS_UN(4, "RTSUn", "RTS Un"), 
	RTS_DEUX(5, "RTSDeux", "RTS Deux"), 
	RSI_LA1(6, "RSILA1", "RSI LA 1"), 
	RSI_LA2(7, "RSILA2", "RSI LA 2");
	
	private static final EChannel DEFAULT = SRF_1;
	
	private final int id;
	private final String url;
	private final String name;
	
	private EChannel(int id, String url, String name) {
		this.id = id;
		this.url = url;
		this.name = name;
	}

	public int getId() {
		return id;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getName() {
		return name;
	}

	public static String[] getAllNames() {
		String result[] = new String[values().length];
		int i = 0;
		for (EChannel channel : values()) {
			result[i] = channel.getName();
			i++;
		}
		return result;
	}
	
	public static String[] getAllUrls() {
		String result[] = new String[values().length];
		int i = 0;
		for (EChannel channel : values()) {
			result[i] = channel.getUrl();
			i++;
		}
		return result;
	}
	
	public static EChannel getById(int id) {
		for (EChannel channel : values()) {
			if (channel.getId() == id) {
				return channel;
			}
		}
		return DEFAULT;
	}

	public static EChannel getByUrl(String url) {
		for (EChannel channel : values()) {
			if (channel.getUrl() == url) {
				return channel;
			}
		}
		return DEFAULT;
	}
}
