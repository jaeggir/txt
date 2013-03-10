package ch.rogerjaeggi.txt.loader;

import static ch.rogerjaeggi.txt.Constants.DEFAULT_SUB_PAGE;
import static ch.rogerjaeggi.txt.loader.EErrorType.CONNECTION_PROBLEM;
import static ch.rogerjaeggi.txt.loader.EErrorType.OTHER_PROBLEM;
import static ch.rogerjaeggi.txt.loader.EErrorType.PAGE_NOT_FOUND;
import static ch.rogerjaeggi.txt.loader.LoadPageTaskFactory.createTask;
import static ch.rogerjaeggi.txt.loader.PageInfo.createFromKey;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
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
	}

	public void removeListener() {
		this.listener = null;
	}

	public synchronized void requestPage(PageKey key) {
		requests.offer(key);
		this.notifyAll();
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
								Logging.d(this, "fetching page: " + task.getKey().getPage() + ", isNotifyUi: " + task.getKey().isNotifyUi());
								TxtResult result = task.execute();
								PageInfo pageInfo = result.getPageInfo();
								if (key.isNotifyUi()) {
									requestPage(new PageKey(pageInfo.getChannel(), pageInfo.getNextPage(), DEFAULT_SUB_PAGE, false, false));
								}
								notifyListener(result, key.isNotifyUi());
							} catch (PageNotFoundException e) {
								notifyListener(e.getPageInfo(), PAGE_NOT_FOUND, key.isNotifyUi());
							} catch (MalformedURLException e) {
								notifyListener(createFromKey(key), OTHER_PROBLEM, key.isNotifyUi());
							} catch (URISyntaxException e) {
								notifyListener(createFromKey(key), OTHER_PROBLEM, key.isNotifyUi());
							} catch (CannotParseImageException e) {
								notifyListener(createFromKey(key), OTHER_PROBLEM, key.isNotifyUi());
							} catch (IOException e) {
								notifyListener(createFromKey(key), CONNECTION_PROBLEM, key.isNotifyUi());
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

	private void notifyListener(TxtResult result, boolean notifyUi) {
		if (notifyUi) {
			if (listener != null) {
				listener.notifyPageLoaded(result);
			} else {
				Logging.d(this, "no listener found!");
			}
		}
	}

	private void notifyListener(PageInfo pageInfo, EErrorType errorType, boolean notifyUi) {
		if (notifyUi) {
			if (listener != null) {
				listener.notifyPageLoadFailed(pageInfo, errorType);
			} else {
				Logging.d(this, "no listener found!");
			}
		}
	}

	public boolean hasRequestInProgress() {
		return requestInProgress;
	}

}
