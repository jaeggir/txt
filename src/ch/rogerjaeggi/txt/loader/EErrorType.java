package ch.rogerjaeggi.txt.loader;

import ch.rogerjaeggi.txt.R;


public enum EErrorType {

	PAGE_NOT_FOUND(R.string.errorPageNotFound),
	
	CONNECTION_PROBLEM(R.string.errorConnectionProblem),
	
	OTHER_PROBLEM(R.string.errorOther);

	private int textResource;
	
	private EErrorType(int textResource) {
		this.textResource = textResource;
	}
	
	public int getTextResource() {
		return textResource;
	}
	
}
