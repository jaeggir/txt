package ch.rogerjaeggi.txt.loader;


public class HttpClientTask /*extends LoadPageTask*/ {

//	public HttpClientTask(PageRequest request) {
//		super(request);
//	}
//
//	@Override
//	protected TxtResult load() throws FileNotFoundException, IOException {
//		
//		AndroidHttpClient client = AndroidHttpClient.newInstance("ch.rogerjaeggi.txt");
//
//		TxtKey key = getPageRequest().getKey();
//		try {
//			
//			HttpUriRequest httpRequest = new HttpGet(new URI(getImageUrl()));
//
//			if (getPageRequest().isForceRefresh()) {
//				httpRequest.addHeader("Cache-Control", "no-cache");
//			}
//			
//			HttpResponse response = client.execute(httpRequest);
//			
//			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
//				if (key.getSubPage() == 0) {
//					key.incrementSubPage();
//					return load();
//				} else {
//					throw new FileNotFoundException();
//				}
//			} else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//				
//				ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
//				try {
//					response.getEntity().writeTo(dataStream);
//					final byte[] imgData = dataStream.toByteArray();
//					BitmapFactory.Options options = new BitmapFactory.Options();
//					TxtResult result = new TxtResult(getPageRequest().getKey(), BitmapFactory.decodeByteArray(imgData, 0, imgData.length, options));
//					
//					result.setPageInfo(getPageInfo());
//					
//					return result;
//				} finally {
//					try { 
//						dataStream.close(); 
//					} catch (IOException e) {
//						// ignore
//					}
//				}
//			} else {
//				Log.e(TAG, "Could not load Bitmap from: " + getImageUrl() + ", responseCode=" + response.getStatusLine().getStatusCode());
//				return new TxtResult(key, new IllegalArgumentException());
//			}
//		} catch (URISyntaxException e) {
//			return new TxtResult(key, e);
//		} finally {
//			client.close();
//		}
//	}
//
//	@Override
//	protected PageInfo getPageInfo() {
//		
//		AndroidHttpClient http = AndroidHttpClient.newInstance("ch.rogerjaeggi.txt");
//		
//		try {
//			HttpUriRequest httpRequest = new HttpGet(new URI(getPageUrl()));
//			
//			if (getPageRequest().isForceRefresh()) {
//				httpRequest.addHeader("Cache-Control", "no-cache");
//			}
//			
//			HttpResponse response = http.execute(httpRequest);
//			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//				InputStreamReader isr = new InputStreamReader(response.getEntity().getContent());
//			    BufferedReader br = new BufferedReader(isr);
//				try {
//					return parsePage(br);
//				} finally {
//				    try {
//				    	isr.close();
//				    } catch (IOException e) {
//				    	// ignore
//				    }
//				}
//			}
//		} catch (IOException e) {
//			Logging.e(this, "IOE", e);
//		} catch (URISyntaxException e) {
//			Logging.e(this, "Invalid URI", e);
//		} finally {
//			http.close();
//		}
//		return null;
//	}

}
