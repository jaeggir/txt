package ch.rogerjaeggi.txt.utils.tasks;

import android.app.Activity;

/**
 * 
 * @author Roger Jaeggi
 *
 * @param <Result>
 */
public interface ITaskActivityCallable<Result> {

	/**
	 * 
	 * @return
	 */
	Activity getActivity();
	
	/**
	 * 
	 * @param result
	 */
	void onDone(Result result);

	/**
	 * 
	 */
	void onCancelled();

	/**
	 * 
	 * @return
	 */
	int getDialogId();
	
}
