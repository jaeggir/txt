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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;


public class UrlConnectionTask extends LoadPageTask {

	public UrlConnectionTask(PageKey key) {
		super(key);

		HttpURLConnection.setFollowRedirects(true);
		
		disableConnectionReuseIfNecessary();
	}

	@Override
	protected Bitmap loadImage(String urlToLoad) throws FileNotFoundException, IOException {

		URL url = new URL(urlToLoad);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		setCacheControl(connection);
		
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
			Bitmap bitmap = BitmapFactory.decodeByteArray(imgData, 0, imgData.length, options);
			if (bitmap == null) {
				throw new IOException("Couldn't decode image");
			} else {
				return bitmap;
			}
		} catch (FileNotFoundException e) {
			throw e;
//			Log.e(TAG, "Could not load Bitmap from: " + url);
//			if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
//				throw new FileNotFoundException(urlToLoad);
//			} else {
//				Logging.d(this, "fetching page " + getKey() + " failed, error code: " + connection.getResponseCode());
//				throw new IllegalArgumentException(urlToLoad);
//			}
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
	protected PageInfo loadPageInfo(String urlToLoad) throws IOException {
			
		URL url = new URL(urlToLoad);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		setCacheControl(connection);
		
		InputStreamReader isr = new InputStreamReader(connection.getInputStream());

		if (!url.getPath().equals(connection.getURL().getPath())) {
			// redirect, update page info
			String path = connection.getURL().getPath();
			String subPage = path.substring(path.length() - 6, path.length() - 5);
			updateSubPage(subPage);
		}
		
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
	}

	private void setCacheControl(HttpURLConnection connection) {
		if (isForceRefresh()) {
			connection.addRequestProperty("Cache-Control", "no-cache");
		}
	}
	
	private void disableConnectionReuseIfNecessary() {
	    // HTTP connection reuse which was buggy pre-froyo (which we here not support..)
	    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
	        System.setProperty("http.keepAlive", "false");
	    }
	}

}
