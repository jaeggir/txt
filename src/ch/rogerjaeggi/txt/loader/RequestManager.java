package ch.rogerjaeggi.txt.loader;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import ch.rogerjaeggi.utils.Logging;

public class RequestManager {

	private Thread worker;

	private final Queue<PageRequest> requests;

//	private final Map<PageRequest, TxtResult> lookup;

	private IRequestListener listener;

	public RequestManager() {
		this.requests = new LinkedBlockingQueue<PageRequest>();
//		this.lookup = new HashMap<PageRequest, TxtResult>();
	}

	public void setListener(IRequestListener listener) {
		this.listener = listener;
		Logging.d(this, "listener added");
	}

	public void removeListener() {
		Logging.d(this, "listener removed");
		this.listener = null;
	}

	public synchronized void requestPage(PageRequest request) {
		if (requests.contains(request)) {
			Logging.d(this, "queue contains request for page " + request.getKey());
		} else {
			requests.offer(request);
			this.notifyAll();
		}
	}

	public void init() {
		this.worker = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					while (true) {
						PageRequest request = requests.poll();
						if (request == null) {
							synchronized(RequestManager.this) {
								RequestManager.this.wait();
							}
						} else {
							LoadPageTask task = LoadPageTaskFactory.createTask(request);
							TxtResult result = task.execute();
							notifyListener(result);
						}
					}
				} catch (InterruptedException e) {
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
