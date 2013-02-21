package ch.rogerjaeggi.txt;


public enum EChannel {
	
	SRF_1(0, "SRF1", "SRF 1"), 
	SRF_ZWEI(1, "SRFzwei", "SRF zwei"), 
	SRF_INFO(2, "SRFinfo", "SRF Info"), 
	RTS_UN(3, "RTSUn", "RTS Un"), 
	RTS_DEUX(4, "RTSDeux", "RTS Deux"), 
	RSI_LA1(5, "RSILA1", "RSI LA 1"), 
	RSI_LA2(6, "RSILA2", "RSI LA 2");
	
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
