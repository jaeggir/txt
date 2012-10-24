package ch.rogerjaeggi.txt;


public enum EChannel {

	SF1("SF1"), 
	SFzwei("SFzwei"), 
	SFInfo("SFinfo"), 
	RTSUn("TSR1"), 
	RTSdeux("TSR2"), 
	RSILA1("RSILA1"), 
	RSILA2("RSILA2");
	
	private final String id;
	
	private EChannel(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
}
