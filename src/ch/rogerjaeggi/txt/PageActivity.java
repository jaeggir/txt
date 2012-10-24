package ch.rogerjaeggi.txt;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;
import ch.rogerjaeggi.txt.R.anim;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class PageActivity extends SherlockActivity {
	
	private static final int GO_TO_CODE = 77;
	
	private static final String BASE_URL = "http://www.teletext.ch/dynpics/";
	
	AsyncTask<Void, Void, Bitmap> loadPageTask;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page);
		setTitle(getTxtApplication().getCurrentChannel().getId() + " - " + getTxtApplication().getCurrentPage());
		View page = findViewById(R.id.page);
		page.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(PageActivity.this, GoToActivity.class);
				startActivityForResult(intent, GO_TO_CODE);
			}
		});
        runLoadPageTask(null, BASE_URL + getTxtApplication().getCurrentChannel().getId() + "/", getCurrentPage(), 0);        
    }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == GO_TO_CODE && RESULT_OK == resultCode) {
			runLoadPageTask(null, BASE_URL + getTxtApplication().getCurrentChannel().getId() + "/", data.getIntExtra(Constants.EXTRA_PAGE, 100), 0);
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
    
    private void runLoadPageTask(final MenuItem item, String baseUrl, final int page, int subIndex) {
    	if (loadPageTask != null) {
    		loadPageTask.cancel(true);
    		loadPageTask = null;
    	}
		loadPageTask = new LoadPageTask(baseUrl, page, subIndex) {
			
			@Override
			protected void onCancelled() {
				loadPageTask = null;
				cancelAnimation();
				invalidateOptionsMenu();
				// TODO
				super.onCancelled();
			}
			
			@Override
		    protected void onPostExecute(Bitmap result) {
				loadPageTask = null;
				if (result == null) {
					Toast.makeText(PageActivity.this, "Page not found", Toast.LENGTH_LONG).show();
				} else {
					ImageView image = (ImageView) findViewById(R.id.page);
					image.setImageBitmap(result);
					image.setVisibility(View.VISIBLE);
					findViewById(R.id.loading).setVisibility(View.GONE);
					findViewById(R.id.loadingText).setVisibility(View.GONE);
					TxtApplication app = (TxtApplication) getApplication();
					app.setCurrentPage(page); // TODO
					setTitle(getTxtApplication().getCurrentChannel().getId() + " - " + getTxtApplication().getCurrentPage());
				}
				cancelAnimation();
				invalidateOptionsMenu();
		    }
			
			private void cancelAnimation() {
				if (item != null && item.getActionView() != null) {
					item.getActionView().clearAnimation();
					item.setActionView(null);
				}
			}
		};
		loadPageTask.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	getSupportMenuInflater().inflate(R.menu.activity_page, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    		case R.id.menu_refresh:
    	    	item.setEnabled(false);
    	    	
    	    	LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	        ImageView iv = (ImageView) inflater.inflate(R.layout.refresh_action_view, null);

    	        Animation rotation = AnimationUtils.loadAnimation(this, anim.refresh_animation);
    	        rotation.setRepeatCount(Animation.INFINITE);
    	        iv.startAnimation(rotation);

    	        item.setActionView(iv);
    	    	
    			runLoadPageTask(item, BASE_URL + getTxtApplication().getCurrentChannel().getId() + "/", getCurrentPage(), 0);    
    			return true;
    		case R.id.menu_backwards:
    	    	item.setEnabled(false);
    			runLoadPageTask(item, BASE_URL + getTxtApplication().getCurrentChannel().getId() + "/", getCurrentPage() - 1, 0);  
    			return true;
    		case R.id.menu_forewards:
    	    	item.setEnabled(false);
    			runLoadPageTask(item, BASE_URL + getTxtApplication().getCurrentChannel().getId() + "/", getCurrentPage() + 1, 0);  
    			return true;
    		case R.id.menu_goto:
    			Intent intent = new Intent(PageActivity.this, GoToActivity.class);
				startActivityForResult(intent, GO_TO_CODE);
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
