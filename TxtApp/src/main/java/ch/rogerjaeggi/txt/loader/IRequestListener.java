package ch.rogerjaeggi.txt.loader;


public interface IRequestListener {

	void notifyPageLoaded(TxtResult result);
	
	void notifyPageLoadFailed(PageInfo pageInfo, EErrorType errorType);
}
