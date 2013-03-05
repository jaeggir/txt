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
import ch.rogerjaeggi.txt.loader.cache.TxtKey;
import ch.rogerjaeggi.utils.Logging;


public class HttpClientTask extends LoadPageTask {

	public HttpClientTask(PageRequest request) {
		super(request);
	}

	@Override
	protected TxtResult fetchPage() throws FileNotFoundException, IOException {
		
		AndroidHttpClient client = AndroidHttpClient.newInstance("ch.rogerjaeggi.txt");
		
		try {
			
			HttpUriRequest httpRequest = new HttpGet(new URI(getImageUrl()));

			if (getPageRequest().isForceRefresh()) {
				httpRequest.addHeader("Cache-Control", "no-cache");
			}
			
			HttpResponse response = client.execute(httpRequest);
			
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
				TxtKey key = getPageRequest().getKey();
				if (key.getSubPage() == 0) {
					key.incrementSubPage();
					return fetchPage();
				} else {
					throw new FileNotFoundException();
				}
			} else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				
				ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
				try {
					response.getEntity().writeTo(dataStream);
					final byte[] imgData = dataStream.toByteArray();
					BitmapFactory.Options options = new BitmapFactory.Options();
					TxtResult result = new TxtResult(getPageRequest().getKey(), BitmapFactory.decodeByteArray(imgData, 0, imgData.length, options));
					if (getPageRequest().isLoadPageLinks()) {
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
				Log.e(TAG, "Could not load Bitmap from: " + getImageUrl() + ", responseCode=" + response.getStatusLine().getStatusCode());
				return new TxtResult(null, null);
			}
		} catch (URISyntaxException e) {
			return new TxtResult(null, null);
		} finally {
			client.close();
		}
	}

	@Override
	protected List<TouchableArea> findTouchableAreas() {
		
		AndroidHttpClient http = AndroidHttpClient.newInstance("ch.rogerjaeggi.txt");
		
		try {
			HttpUriRequest httpRequest = new HttpGet(new URI(getPageUrl()));
			
			if (getPageRequest().isForceRefresh()) {
				httpRequest.addHeader("Cache-Control", "no-cache");
			}
			
			HttpResponse response = http.execute(httpRequest);
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
