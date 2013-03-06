package ch.rogerjaeggi.txt.loader;

import static ch.rogerjaeggi.txt.loader.LoadPageTaskFactory.createTask;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import ch.rogerjaeggi.utils.Logging;

public class RequestManager {

	private Thread worker;

	private final Queue<PageKey> requests;

	private IRequestListener listener;

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
							LoadPageTask task = createTask(key);
							TxtResult result = task.execute();
							notifyListener(result);
						}
					}
				} catch (InterruptedException e) {
					// TODO error handling
					e.printStackTrace();
				}
			}

		});
		worker.start();
	}

	private void notifyListener(TxtResult result) {
		if (listener != null) {
			listener.notifyLoaded(result);
		} else {
			Logging.d(this, "no listener found!");
		}
	}

}
