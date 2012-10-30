package ch.rogerjaeggi.txt.loader;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import ch.rogerjaeggi.txt.EChannel;
import ch.rogerjaeggi.utils.Logging;


public class UrlConnectionTask extends LoadPageTask {

	public UrlConnectionTask(Context context, EChannel channel, int page, int subPage, boolean forceRefresh) {
		super(channel, page, subPage, forceRefresh);

		disableConnectionReuseIfNecessary();
		enableHttpResponseCache(context);
	}

	@Override
	protected TxtResult doWork() throws FileNotFoundException, IOException {
		try {
			URL url = new URL(BASE_URL + "dynpics/" + channel.getUrl() + "/" + page + "-0" + subPage + ".gif");
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
				TxtResult result = new TxtResult(BitmapFactory.decodeByteArray(imgData, 0, imgData.length, options));
				result.addTouchableAreas(findTouchableAreas());
				return result;
			} catch (FileNotFoundException e) {
				if (subPage == 0) {
					subPage++;
					return doWork();
				}
				Log.e(TAG, "Could not load Bitmap from: " + BASE_URL + "dynpics/" + channel + "/" + page + "-0" + subPage + ".gif");
				if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
					throw e;
				} else {
					return new TxtResult(null);
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

	@Override
	protected List<TouchableArea> findTouchableAreas() {
		try {
			URL url = new URL(BASE_URL  + channel.getUrl() + "/" + page + "-0" + subPage + ".html");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			if (forceRefresh) {
				connection.addRequestProperty("Cache-Control", "no-cache");
			}
			
			InputStreamReader isr = new InputStreamReader(connection.getInputStream());
		    BufferedReader br = new BufferedReader(isr);

			try {
				return parsePage(br);
			} finally {
			    try {
			    	isr.close();
			    } catch (IOException e) {
			    	// ignore
			    }
				connection.disconnect();
			}
		} catch (IOException e) {
			Logging.e(this, "IOE", e);
			return null;
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
