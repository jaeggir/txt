package ch.rogerjaeggi.txt.loader;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import ch.rogerjaeggi.utils.Logging;


public class UrlConnectionTask extends LoadPageTask {

	public UrlConnectionTask(PageRequest request) {
		super(request);

		disableConnectionReuseIfNecessary();
	}

	@Override
	protected TxtResult fetchPage() throws FileNotFoundException, IOException {
		
		HttpURLConnection connection = (HttpURLConnection) new URL(getImageUrl()).openConnection();
		if (getPageRequest().isForceRefresh()) {
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
			TxtResult result = new TxtResult(getPageRequest().getKey(), BitmapFactory.decodeByteArray(imgData, 0, imgData.length, options));
			if (getPageRequest().isLoadPageLinks()) {
				result.addTouchableAreas(findTouchableAreas());
			}
			return result;
		} catch (FileNotFoundException e) {
			
			if (getPageRequest().getKey().getSubPage() == 0) {
				getPageRequest().getKey().incrementSubPage();
				return fetchPage();
			}
			
			Log.e(TAG, "Could not load Bitmap from: " + getImageUrl());
			if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
				throw e;
			} else {
				Logging.d(this, "fetching page " + getPageRequest().getKey() + " failed, error code: " + connection.getResponseCode());
				return new TxtResult(getPageRequest().getKey(), new IllegalArgumentException());
			}
		} finally {
			try { 
				dataStream.close(); 
			} catch (IOException e) {
				// ignore
			}
			connection.disconnect();
		}
	}

	@Override
	protected List<TouchableArea> findTouchableAreas() {
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(getPageUrl()).openConnection();
			if (getPageRequest().isForceRefresh()) {
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

}
