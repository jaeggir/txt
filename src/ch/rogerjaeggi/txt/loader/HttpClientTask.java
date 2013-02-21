package ch.rogerjaeggi.txt.loader;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.util.Log;
import ch.rogerjaeggi.txt.EChannel;
import ch.rogerjaeggi.utils.Logging;


public class HttpClientTask extends LoadPageTask {

	public HttpClientTask(EChannel channel, int page, int subPage, boolean loadPageLinks, boolean forceRefresh) {
		super(channel, page, subPage, loadPageLinks, forceRefresh);
	}

	@Override
	protected TxtResult doWork() throws FileNotFoundException, IOException {
		AndroidHttpClient http = AndroidHttpClient.newInstance("ch.rogerjaeggi.txt");
		try {
			URI uri = new URI(BASE_URL + "dynpics/" + channel.getUrl() + "/" + page + "-0" + subPage + ".gif");
			HttpUriRequest request = new HttpGet(uri);
			if (forceRefresh) {
				request.addHeader("Cache-Control", "no-cache");
			}
			HttpResponse response = http.execute(request);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
				if (subPage == 0) {
					subPage++;
					return doWork();
				} else {
					throw new FileNotFoundException();
				}
			} else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
				try {
					response.getEntity().writeTo(dataStream);
					final byte[] imgData = dataStream.toByteArray();
					BitmapFactory.Options options = new BitmapFactory.Options();
					TxtResult result = new TxtResult(BitmapFactory.decodeByteArray(imgData, 0, imgData.length, options));
					if (loadPageLinks) {
						result.addTouchableAreas(findTouchableAreas());
					}
					return result;
				} finally {
					try { 
						dataStream.close(); 
					} catch (IOException e) {
						// ignore
					}
				}
			} else {
				Log.e(TAG, "Could not load Bitmap from: " + BASE_URL + "dynpics/" + channel + "/" + page + "-0" + subPage + ".gif, responseCode=" + response.getStatusLine().getStatusCode());
				return new TxtResult(null);
			}
		} catch (URISyntaxException e) {
			return new TxtResult(null);
		} finally {
			http.close();
		}
	}

	@Override
	protected List<TouchableArea> findTouchableAreas() {
		AndroidHttpClient http = AndroidHttpClient.newInstance("SimpleTxt");
		try {
			URI uri = new URI(BASE_URL  + channel.getUrl() + "/" + page + "-0" + subPage + ".html");
			HttpUriRequest request = new HttpGet(uri);
			if (forceRefresh) {
				request.addHeader("Cache-Control", "no-cache");
			}
			HttpResponse response = http.execute(request);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				InputStreamReader isr = new InputStreamReader(response.getEntity().getContent());
			    BufferedReader br = new BufferedReader(isr);
				try {
					return parsePage(br);
				} finally {
				    try {
				    	isr.close();
				    } catch (IOException e) {
				    	// ignore
				    }
				}
			}
		} catch (IOException e) {
			Logging.e(this, "IOE", e);
		} catch (URISyntaxException e) {
			Logging.e(this, "Invalid URI", e);
		} finally {
			http.close();
		}
		return null;
	}

}
