package ch.rogerjaeggi.txt.loader;


public interface IRequestListener {

	void notifyLoaded(TxtResult result);
	
	// TODO notifyFailed(TxtResult result);

}
