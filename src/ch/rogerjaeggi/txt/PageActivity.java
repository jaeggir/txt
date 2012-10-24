package ch.rogerjaeggi.txt;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;

public class PageActivity extends Activity implements OnNavigationListener {
	
	private static final String BASE_URL = "http://www.teletext.ch/dynpics/";
	
	private static final String TAG = "txt.pageActivity";
	
	AsyncTask<Void, Void, Bitmap> loadPageTask;
	
    @TargetApi(11)
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page);
		setTitle("");
        
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        SpinnerAdapter spinnerAdapter = ArrayAdapter.createFromResource(actionBar.getThemedContext(), R.array.channels, android.R.layout.simple_spinner_dropdown_item);
        actionBar.setListNavigationCallbacks(spinnerAdapter, this);
        
        runLoadPageTask(BASE_URL + getTxtApplication().getCurrentChannel().getId() + "/", getCurrentPage(), 0);        
    }

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		Log.d(TAG, "itemPosition=" + itemPosition + ", itemId=" + itemId);
		EChannel channel = EChannel.values()[itemPosition];
		getTxtApplication().setCurrentChannel(channel);
		getTxtApplication().setCurrentPage(100);
		runLoadPageTask(BASE_URL + getTxtApplication().getCurrentChannel().getId() + "/", getCurrentPage(), 0);
		return true;
	}
    
    private void runLoadPageTask(String baseUrl, final int page, int subIndex) {
    	if (loadPageTask != null) {
    		loadPageTask.cancel(true);
    		loadPageTask = null;
    	}
		loadPageTask = new LoadPageTask(baseUrl, page, subIndex) {
			
			@Override
		    protected void onPostExecute(Bitmap result) {
				loadPageTask = null;
				ImageView image = (ImageView) findViewById(R.id.page);
				image.setImageBitmap(result);
				image.setVisibility(View.VISIBLE);
				findViewById(R.id.loading).setVisibility(View.GONE);
				findViewById(R.id.loadingText).setVisibility(View.GONE);
				TxtApplication app = (TxtApplication) getApplication();
				app.setCurrentPage(page); // TODO
				invalidateOptionsMenu();
		    }
		};
		loadPageTask.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_page, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    		case R.id.menu_refresh:
                item.setActionView(R.layout.indeterminate_progress_action);
    	    	item.setEnabled(false);
    			runLoadPageTask(BASE_URL + getTxtApplication().getCurrentChannel().getId() + "/", getCurrentPage(), 0);    
    			return true;
    		case R.id.menu_backwards:
    	    	item.setEnabled(false);
    			runLoadPageTask(BASE_URL + getTxtApplication().getCurrentChannel().getId() + "/", getCurrentPage() - 1, 0);  
    			return true;
    		case R.id.menu_forewards:
    	    	item.setEnabled(false);
    			runLoadPageTask(BASE_URL + getTxtApplication().getCurrentChannel().getId() + "/", getCurrentPage() + 1, 0);  
    			return true;
    		default:
    			return super.onOptionsItemSelected(item);
    	}
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	MenuItem item = menu.findItem(R.id.menu_backwards);
    	item.setEnabled(getCurrentPage() != 100);
    	
    	item = menu.findItem(R.id.menu_forewards);
    	item.setEnabled(getCurrentPage() != 89); // TODO set background image
    	
    	item = menu.findItem(R.id.menu_refresh);
    	item.setEnabled(true);
    	
    	return super.onPrepareOptionsMenu(menu);
    }
    
    private TxtApplication getTxtApplication() {
    	return (TxtApplication) getApplication();
    }
    
    private int getCurrentPage() {
    	return getTxtApplication().getCurrentPage();
    }
    
    @Override
    protected void onDestroy() {
        if (loadPageTask != null) {
        	loadPageTask.cancel(true);
        }
        super.onDestroy();
    }
}
