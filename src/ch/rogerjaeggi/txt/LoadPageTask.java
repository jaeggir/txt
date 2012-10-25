package ch.rogerjaeggi.txt;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

public abstract class LoadPageTask extends AsyncTask<Void, Void, Bitmap> {

	private static final int IO_BUFFER_SIZE = 4 * 1024;

	private static final String TAG = "txt.pageActivity";

	private final String baseUrl;
	private final int page;
	private int subIndex;

	public LoadPageTask(Context context, String baseUrl, int page, int subIndex) {
		this.baseUrl = baseUrl;
		this.page = page;
		this.subIndex = subIndex;
		
		disableConnectionReuseIfNecessary();
		enableHttpResponseCache(context);
	}

	@Override
	protected Bitmap doInBackground(Void... params) {
		return loadPageWithUrlConnection();
	}

	private Bitmap loadPageWithUrlConnection() {
		Bitmap bitmap = null;
		try {
			URL url = new URL(baseUrl + page + "-0" + subIndex + ".gif");
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
			try {
				InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				final byte[] data = new byte[IO_BUFFER_SIZE];
				int read = 0;
				while ((read = in.read(data)) != -1) {
					dataStream.write(data, 0, read);
				}
				final byte[] imgData = dataStream.toByteArray();
				BitmapFactory.Options options = new BitmapFactory.Options();
				bitmap = BitmapFactory.decodeByteArray(imgData, 0, imgData.length, options);
			} finally {
				dataStream.close();
				urlConnection.disconnect();
			}
		} catch (FileNotFoundException e) {
			if (subIndex == 0) {
				subIndex++;
				return loadPageWithUrlConnection();
			}
			Log.e(TAG, "Could not load Bitmap from: " + baseUrl + page + "-0" + subIndex + ".gif");
		} catch (MalformedURLException e) {
			e.printStackTrace();
			Log.e(TAG, "Could not load Bitmap from: " + baseUrl + page + "-0" + subIndex + ".gif");
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "Could not load Bitmap from: " + baseUrl + page + "-0" + subIndex + ".gif");
		} 
		return bitmap;
	}
	
	private void disableConnectionReuseIfNecessary() {
	    // HTTP connection reuse which was buggy pre-froyo
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
	    }
	}

}
