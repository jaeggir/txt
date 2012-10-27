package ch.rogerjaeggi.txt;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.Build;
import android.util.Log;
import ch.rogerjaeggi.utils.tasks.BetterTask;

public abstract class LoadPageTask extends BetterTask<Void, Void, Bitmap> {

	private static final int IO_BUFFER_SIZE = 4 * 1024;

	private static final String TAG = "txt.pageActivity";
	
	private final String baseUrl;
	private final int page;
	private int subIndex;
	private final boolean forceRefresh;

	private FileNotFoundException error; 
	
	public LoadPageTask(Context context, String baseUrl, int page, int subIndex, boolean forceRefresh) {
		this.baseUrl = baseUrl;
		this.page = page;
		this.subIndex = subIndex;
		this.forceRefresh = forceRefresh;
		
		disableConnectionReuseIfNecessary();
		enableHttpResponseCache(context);
	}
	
	public int getSubIndex() { 
		return subIndex;
	}

	public int getPage() {
		return page;
	}
	
	public void cancelRefreshIndicators() {
		// empty, override if needed
	}
	
	@Override
	protected Bitmap doInBackground(Void... params) {
		try {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
				return loadBitmapWithApacheHttpClient();
			} else {
				return loadBitmapWithUrlConnection();
			}
		} catch (FileNotFoundException e) {
			error = e;
			return null;
		} catch (IOException e) {
			return null;
		}
	}
	
	public boolean doesPageExists() {
		return error == null;
	}

	private Bitmap loadBitmapWithUrlConnection() throws IOException, FileNotFoundException {
		try {
			URL url = new URL(baseUrl + page + "-0" + subIndex + ".gif");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			if (forceRefresh) {
				connection.addRequestProperty("Cache-Control", "no-cache");
			}
			ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
			try {
				InputStream in = new BufferedInputStream(connection.getInputStream());
				final byte[] data = new byte[IO_BUFFER_SIZE];
				int read = 0;
				while ((read = in.read(data)) != -1) {
					dataStream.write(data, 0, read);
				}
				final byte[] imgData = dataStream.toByteArray();
				BitmapFactory.Options options = new BitmapFactory.Options();
				return BitmapFactory.decodeByteArray(imgData, 0, imgData.length, options);
			} catch (FileNotFoundException e) {
				if (subIndex == 0) {
					subIndex++;
					return loadBitmapWithUrlConnection();
				}
				Log.e(TAG, "Could not load Bitmap from: " + baseUrl + page + "-0" + subIndex + ".gif, responseCode=" + connection.getResponseCode());
				if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
					throw e;
				} else {
					return null;
				}
			} finally {
				try { 
					dataStream.close(); 
				} catch (IOException e) {
					// ignore
				}
				connection.disconnect();
			}
		} catch (FileNotFoundException e) {
			throw e;
		}
	}
	
	private Bitmap loadBitmapWithApacheHttpClient() throws IOException, FileNotFoundException {
		AndroidHttpClient http = AndroidHttpClient.newInstance("SimpleTxt");
		try {
			URI uri = new URI(baseUrl + page + "-0" + subIndex + ".gif");
			HttpUriRequest request = new HttpGet(uri);
			HttpResponse response = http.execute(request);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
				if (subIndex == 0) {
					subIndex++;
					return loadBitmapWithApacheHttpClient();
				} else {
					throw new FileNotFoundException();
				}
			} else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
				try {
					response.getEntity().writeTo(dataStream);
					final byte[] imgData = dataStream.toByteArray();
					BitmapFactory.Options options = new BitmapFactory.Options();
					return BitmapFactory.decodeByteArray(imgData, 0, imgData.length, options);
				} finally {
					try { 
						dataStream.close(); 
					} catch (IOException e) {
						// ignore
					}
				}
			} else {
				Log.e(TAG, "Could not load Bitmap from: " + baseUrl + page + "-0" + subIndex + ".gif, responseCode=" + response.getStatusLine().getStatusCode());
				return null;
			}
		} catch (URISyntaxException e) {
			return null;
		} finally {
			http.close();
		}
	}
	
	private void disableConnectionReuseIfNecessary() {
	    // HTTP connection reuse which was buggy pre-froyo (which we here not support..)
	    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
	        System.setProperty("http.keepAlive", "false");
	    }
	}
	
	private void enableHttpResponseCache(Context context) {
	    try {
	        long httpCacheSize = 1 * 1024 * 1024; // 1 MiB
	        File httpCacheDir = new File(context.getCacheDir(), "http");
	        Class.forName("android.net.http.HttpResponseCache").getMethod("install", File.class, long.class).invoke(null, httpCacheDir, httpCacheSize);
	    } catch (Exception httpResponseCacheNotAvailable) {
	    	// ignore
	    }
	}
	
}
