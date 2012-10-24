package ch.rogerjaeggi.txt;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

public abstract class LoadPageTask extends AsyncTask<Void, Void, Bitmap> {

	private static final int IO_BUFFER_SIZE = 4 * 1024;

	private static final String TAG = "txt.pageActivity";

	private final String baseUrl;
	private final int page;
	private int subIndex;
	
	public LoadPageTask(String baseUrl, int page, int subIndex) {
		this.baseUrl = baseUrl;
		this.page = page;
		this.subIndex = subIndex;
	}
	
	@Override
	protected Bitmap doInBackground(Void... params) {
		return loadPage();
	}
	
	private Bitmap loadPage() {
		
	    Bitmap bitmap = null;
	    InputStream in = null;
	    BufferedOutputStream out = null;
		
		 try {
				URL pageToLoad = new URL(baseUrl + page + "-0" + subIndex + ".gif");
		        in = new BufferedInputStream(pageToLoad.openStream(), IO_BUFFER_SIZE);

		        final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
		        out = new BufferedOutputStream(dataStream, IO_BUFFER_SIZE);
		        copy(in, out);
		        out.flush();

		        final byte[] data = dataStream.toByteArray();
		        BitmapFactory.Options options = new BitmapFactory.Options();
		        //options.inSampleSize = 1;

		        bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,options);
		 	} catch (FileNotFoundException e) {
		 		if (subIndex == 0) {
		 			subIndex++;
		 			return loadPage();
		 		}
		        Log.e(TAG, "Could not load Bitmap from: " + baseUrl + page + "-0" + subIndex + ".gif");
		 	} catch (IOException e) {
		    	e.printStackTrace(); // TODO
		        Log.e(TAG, "Could not load Bitmap from: " + baseUrl + page + "-0" + subIndex + ".gif");
		    } finally {
		        closeStream(in);
		        closeStream(out);
		    }
		    return bitmap;
	}

	private void closeStream(BufferedOutputStream out) {
		if (out != null) {
			try {
				out.close();
			} catch (IOException e) {
				// ignore, we are closing anyway
			}
		}
	}

	private void closeStream(InputStream in) {
		if (in != null) {
			try {
				in.close();
			} catch (IOException e) {
				// ignore, we are closing anyway
			}
		}
	}

	private int copy(final InputStream input, final OutputStream output) throws IOException {
		final byte[] stuff = new byte[1024];
		int read = 0;
		int total = 0;
		while ((read = input.read(stuff)) != -1) {
			output.write(stuff, 0, read);
			total += read;
		}
		return total;
	}

}
