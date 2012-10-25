package ch.rogerjaeggi.txt;


public enum EChannel {
	
	
	SF1(0, "SF1", "SF 1"), 
	SFzwei(1, "SFzwei", "SF zwei"), 
	SFInfo(2, "SFinfo", "SF Info"), 
	RTSUn(3, "TSR1", "RTS Un"), 
	RTSdeux(4, "TSR2", "RTS Deux"), 
	RSILA1(5, "RSILA1", "RSI LA 1"), 
	RSILA2(6, "RSILA2", "RSI LA 2");
	
	private static final EChannel DEFAULT = SF1;
	
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
