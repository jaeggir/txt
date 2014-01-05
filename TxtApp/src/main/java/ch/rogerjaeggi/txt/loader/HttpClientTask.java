package ch.rogerjaeggi.txt.loader;

import static android.net.http.AndroidHttpClient.newInstance;
import static org.apache.http.HttpStatus.SC_MOVED_PERMANENTLY;
import static org.apache.http.HttpStatus.SC_MOVED_TEMPORARILY;
import static org.apache.http.HttpStatus.SC_OK;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

import android.graphics.Bitmap;
import android.net.http.AndroidHttpClient;


public class HttpClientTask extends LoadPageTask {

	public HttpClientTask(PageKey key) {
		super(key);
	}

	@Override
	protected PageInfo loadPageInfo(String url) throws RedirectException, URISyntaxException, IOException {
		AndroidHttpClient http = newInstance("ch.rogerjaeggi.txt");

		try {
			HttpUriRequest httpRequest = new HttpGet(new URI(url));
			setCacheControl(httpRequest);
			
			HttpResponse response = http.execute(httpRequest);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == SC_OK) {
				InputStreamReader isr = new InputStreamReader(response.getEntity().getContent());
			    BufferedReader br = new BufferedReader(isr);
				return parsePage(br);
			} else if (statusCode == SC_MOVED_PERMANENTLY || statusCode == SC_MOVED_TEMPORARILY) {
				Header[] headers = response.getHeaders("Location");
	            if (headers != null && headers.length != 0) {
	                String path = headers[headers.length - 1].getValue();
	    			throw handleRedirect(path);
	            } else {
	    			throw new IOException("Redirect to malformed redirect URL detected.");
	            }
			} else {
				throw new IOException("Loading page failed. ErrorCode: " + statusCode);
			}
		} finally {
			http.close();
		}
	}

	@Override
	protected Bitmap loadImage(String url) throws FileNotFoundException, URISyntaxException, CannotParseImageException, IOException {
		AndroidHttpClient http = newInstance("ch.rogerjaeggi.txt");

		try {
			HttpUriRequest httpRequest = new HttpGet(new URI(url));
			setCacheControl(httpRequest);
			
			HttpResponse response = http.execute(httpRequest);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == SC_OK) {
				ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
				try {
					response.getEntity().writeTo(dataStream);
					return decodeBitmap(dataStream);
				} finally {
					try { dataStream.close(); } catch (IOException e) { }
				}
			} else if (statusCode == HttpStatus.SC_NOT_FOUND) {
				throw new FileNotFoundException();
			} else {
				throw new IOException("Loading page failed. ErrorCode: " + statusCode);
			}
		} finally {
			http.close();
		}
	}	
	
	private void setCacheControl(HttpRequest httpRequest) {
		if (isForceRefresh()) {
			httpRequest.addHeader("Cache-Control", "no-cache");
		}
	}

}
