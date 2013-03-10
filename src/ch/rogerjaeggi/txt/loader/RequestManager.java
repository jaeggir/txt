package ch.rogerjaeggi.txt.loader;

import static ch.rogerjaeggi.txt.loader.EErrorType.CONNECTION_PROBLEM;
import static ch.rogerjaeggi.txt.loader.EErrorType.PAGE_NOT_FOUND;
import static ch.rogerjaeggi.txt.loader.LoadPageTaskFactory.createTask;
import static ch.rogerjaeggi.txt.loader.PageInfo.createFromKey;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import ch.rogerjaeggi.utils.Logging;

public class RequestManager {

	private Thread worker;

	private final Queue<PageKey> requests;

	private IRequestListener listener;
	
	private volatile boolean requestInProgress;

	public RequestManager() {
		this.requests = new LinkedBlockingQueue<PageKey>();
	}

	public void setListener(IRequestListener listener) {
		this.listener = listener;
		Logging.d(this, "listener added");
	}

	public void removeListener() {
		Logging.d(this, "listener removed");
		this.listener = null;
	}

	public synchronized void requestPage(PageKey key) {
		if (requests.contains(key)) {
			Logging.d(this, "queue contains request for page " + key);
		} else {
			requests.offer(key);
			this.notifyAll();
		}
	}

	public void init() {
		this.worker = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					while (true) {
						PageKey key = requests.poll();
						if (key == null) {
							synchronized(RequestManager.this) {
								RequestManager.this.wait();
							}
						} else {
							requestInProgress = true;
							LoadPageTask task = createTask(key);
							try {
								TxtResult result = task.execute();
								notifyListener(result);
							} catch (PageNotFoundException e) {
								Logging.d(this, "PageNotFound", e);
								notifyListener(e.getPageInfo(), PAGE_NOT_FOUND);
							} catch (IOException e) {
								Logging.d(this, "IOE", e);
								notifyListener(createFromKey(key), CONNECTION_PROBLEM);
							} finally {
								requestInProgress = false;
							}
						}
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}

		});
		worker.start();
	}

	private void notifyListener(TxtResult result) {
		if (listener != null) {
			listener.notifyPageLoaded(result);
		} else {
			Logging.d(this, "no listener found!");
		}
	}

	private void notifyListener(PageInfo pageInfo, EErrorType errorType) {
		if (listener != null) {
			listener.notifyPageLoadFailed(pageInfo, errorType);
		} else {
			Logging.d(this, "no listener found!");
		}
	}

	public boolean hasRequestInProgress() {
		return requestInProgress;
	}

}
