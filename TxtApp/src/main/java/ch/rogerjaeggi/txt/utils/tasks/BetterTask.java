package ch.rogerjaeggi.txt.utils.tasks;

import android.os.AsyncTask;

/**
 * 
 * @author Roger Jaeggi
 *
 * @param <Params>
 * @param <Progress>
 * @param <Result>
 */
public abstract class BetterTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

	private ITaskActivityCallable<Result> callable;
	
	public BetterTask() {
		super();
	}
	
	public void connect(ITaskActivityCallable<Result> callable) {
		disconnect();
		this.callable = callable;
	}
	
	@Override
	protected void onPreExecute() {
		if (callable != null && callable.getDialogId() != 0) {
			callable.getActivity().showDialog(callable.getDialogId());
		}
	}
	
	public void disconnect() {
		removeDialog();
		callable = null;
	}
	
	@Override
	protected void onPostExecute(Result result) {
		if (callable != null) {
			callable.onDone(result);
		}
		removeDialog();
	}
	
	@Override
	protected void onCancelled() {
		if (callable != null) {
			callable.onCancelled();
		}
		removeDialog();
	}

	private void removeDialog() {
		if (callable != null && callable.getDialogId() != 0) {
			callable.getActivity().removeDialog(callable.getDialogId());
		}
	}
	
	protected ITaskActivityCallable<Result> getCallable() {
		return callable;
	}
}