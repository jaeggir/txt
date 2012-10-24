package ch.rogerjaeggi.txt;

import android.app.Application;


public class TxtApplication extends Application {

	private EChannel currentChannel = EChannel.SF1;
	
	private int currentPage = 100;
	
	public void setCurrentPage(int page) {
		this.currentPage = page;
	}
	
	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentChannel(EChannel channel) {
		this.currentChannel = channel;
	}
	
	public EChannel getCurrentChannel() {
		return currentChannel;
	}
	
}
