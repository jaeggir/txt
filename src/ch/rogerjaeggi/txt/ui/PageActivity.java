package ch.rogerjaeggi.txt.ui;

import java.util.Calendar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import ch.rogerjaeggi.txt.Constants;
import ch.rogerjaeggi.txt.LoadPageTask;
import ch.rogerjaeggi.txt.R;
import ch.rogerjaeggi.txt.R.anim;
import ch.rogerjaeggi.txt.Settings;
import ch.rogerjaeggi.txt.TxtApplication;
import ch.rogerjaeggi.txt.menu.NextMenuUpdater;
import ch.rogerjaeggi.txt.menu.PrevMenuUpdater;
import ch.rogerjaeggi.txt.menu.RefreshMenuUpdater;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class PageActivity extends SherlockActivity implements OnClickListener {

	private static final int DIALOG_CREDITS = 1;
	
	private static final int GO_TO_CODE = 77;
	private static final int GO_TO_SETTINGS = 78;
	
	private static final int SWIPE_MIN_DISTANCE = 50;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;

	private static final String BASE_URL = "http://www.teletext.ch/dynpics/";
	
	private PrevMenuUpdater prevMenuUpdater;
	private NextMenuUpdater nextMenuUpdater;
	private RefreshMenuUpdater refreshMenuUpdater;
	
	LoadPageTask task;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_page);
		
		setTitle(Integer.toString(getCurrentPage()));

		// Gesture detection
		final GestureDetector gestureDetector = new GestureDetector(this, new SimpleOnGestureListener() {

			@Override
			public boolean onSingleTapConfirmed(MotionEvent e) {
				Intent intent = new Intent(PageActivity.this, GoToActivity.class);
				startActivityForResult(intent, GO_TO_CODE);
				return true;
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
				try {
					if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
						if (getCurrentPageIndex() > 0 && getCurrentPage() != 100) {
							runLoadPageTask(BASE_URL + Settings.getChannel(PageActivity.this).getUrl() + "/", getCurrentPage(), getCurrentPageIndex() - 1);
							return true;
						}
					} else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
						runLoadPageTask(BASE_URL + Settings.getChannel(PageActivity.this).getUrl() + "/", getCurrentPage(), getCurrentPageIndex() + 1);
						return true;
					} else if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
						int nextPage =getCurrentPage() < 899 ? getCurrentPage() + 1 : 100;
						runLoadPageTask(BASE_URL + Settings.getChannel(PageActivity.this).getUrl() + "/", nextPage, 0);
						return true;
					} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
						if (getCurrentPage() > 100) {
							runLoadPageTask(BASE_URL + Settings.getChannel(PageActivity.this).getUrl() + "/", getCurrentPage() - 1, 0);
							return true;
						}
					}
				} catch (Exception e) {
					// nothing
				}
				return false;
			}

		});
		gestureDetector.setIsLongpressEnabled(false);
		
		View page = findViewById(R.id.page);
		page.setOnClickListener(this);
		page.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		});
		runLoadPageTask(BASE_URL + Settings.getChannel(PageActivity.this).getUrl() + "/", getCurrentPage(), getCurrentPageIndex());	

		((TxtApplication) getApplication()).setCurrentPage(100); // to handle orientation changes, runLoadPageTask re-sets the page when done
	}

	@Override
	public void onClick(View v) {
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == GO_TO_CODE && RESULT_OK == resultCode) {
			runLoadPageTask(BASE_URL + Settings.getChannel(PageActivity.this).getUrl() + "/", data.getIntExtra(Constants.EXTRA_PAGE, 100), 0);
		} else if (requestCode == GO_TO_SETTINGS && RESULT_OK == resultCode) {
			runLoadPageTask(BASE_URL + Settings.getChannel(PageActivity.this).getUrl() + "/", 100, 0);
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	private void runLoadPageTask(String baseUrl, final int page, int subIndex) {
		if (task != null) {
			task.cancelRefreshMenuEntryAnimation();
			task.cancel(true);
		}
		task = new LoadPageTask(this, baseUrl, page, subIndex) {

			private void startRefreshMenuEntryAnimation() {
				if (refreshMenuUpdater != null) {
					LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					ImageView iv = (ImageView) inflater.inflate(R.layout.refresh_action_view, null);
					
					Animation rotation = AnimationUtils.loadAnimation(PageActivity.this, anim.refresh_animation);
					rotation.setRepeatCount(Animation.INFINITE);
					iv.startAnimation(rotation);
			
					refreshMenuUpdater.getMenuItem().setActionView(iv);
				}
			}

			@Override
			public void cancelRefreshMenuEntryAnimation() {
				if (refreshMenuUpdater != null && refreshMenuUpdater.getMenuItem().getActionView() != null) {
					refreshMenuUpdater.getMenuItem().getActionView().clearAnimation();
					refreshMenuUpdater.getMenuItem().setActionView(null);
				}
			}
			
			@Override
			protected void onPreExecute() {
				startRefreshMenuEntryAnimation();
				super.onPreExecute();
			}
			
			@Override
			protected void onCancelled() {
				cancelRefreshMenuEntryAnimation();
				updateMenuItems();
				super.onCancelled();
			}

			@Override
			protected void onPostExecute(Bitmap result) {
				task = null;
				findViewById(R.id.loading).setVisibility(View.GONE);
				findViewById(R.id.loadingText).setVisibility(View.GONE);
				ImageView image = (ImageView) findViewById(R.id.page);
				TxtApplication app = (TxtApplication) getApplication();
				if (result == null) {
					if (getCurrentPage() != page) {
						image.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.page_does_not_exists));
						findViewById(R.id.errorText).setVisibility(View.VISIBLE);
						if (doesPageExists()) {
							((TextView) findViewById(R.id.errorText)).setText(R.string.errorConnectionProblem);
						} else {
							((TextView) findViewById(R.id.errorText)).setText(String.format(getString(R.string.errorPageNotFound), page));
						}
					} else {
						// ignore
					}
				} else {
					findViewById(R.id.errorText).setVisibility(View.GONE);
					app.setCurrentPageIndex(getSubIndex());
					image.setImageBitmap(result);
				}
				image.setVisibility(View.VISIBLE);
				image.invalidate();
				app.setCurrentPage(page);
				setTitle(Integer.toString(getCurrentPage()));
				
				cancelRefreshMenuEntryAnimation();
				updateMenuItems();
			}
		};
		task.execute();
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
				runLoadPageTask(BASE_URL + Settings.getChannel(PageActivity.this).getUrl() + "/", getCurrentPage(), getCurrentPageIndex());
				return true;
			case R.id.menu_backwards:
				item.setEnabled(false);
				runLoadPageTask(BASE_URL + Settings.getChannel(PageActivity.this).getUrl() + "/", getCurrentPage() - 1, 0);
				return true;
			case R.id.menu_forewards:
				item.setEnabled(false);
				runLoadPageTask(BASE_URL + Settings.getChannel(PageActivity.this).getUrl() + "/", getCurrentPage() + 1, 0);
				return true;
			case R.id.menu_credits:
				showDialog(DIALOG_CREDITS);
				return true;
			case R.id.menu_settings:
				Intent settings = new Intent(PageActivity.this, SettingsActivity.class);
				startActivityForResult(settings, GO_TO_SETTINGS);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
			case DIALOG_CREDITS:
				  ((TextView)dialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
				  break;
			default:
				super.onPrepareDialog(id, dialog);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
			case DIALOG_CREDITS:
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(getString(R.string.menu_credits));
				String msg = "<b>" + getString(R.string.credits_programming) + ":</b> Roger J&auml;ggi, <a href=http://www.rogerjaeggi.ch>www.rogerjaeggi.ch</a><br /><b>" + getString(R.string.credits_data) + ":</b> SwissTXT, <a href=http://www.swisstxt.ch/>www.swisstxt.ch</a><br /><br />&copy;" + Calendar.getInstance().get(Calendar.YEAR) + " Roger J&auml;ggi, SwissTXT";
				builder.setMessage(Html.fromHtml(msg.toString()));
				builder.setCancelable(true);
				return builder.create();
			default:
				return super.onCreateDialog(id);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		prevMenuUpdater = new PrevMenuUpdater(menu.findItem(R.id.menu_backwards));
		prevMenuUpdater.update(getCurrentPage());

		nextMenuUpdater = new NextMenuUpdater(menu.findItem(R.id.menu_forewards));
		nextMenuUpdater.update(getCurrentPage());

		refreshMenuUpdater = new RefreshMenuUpdater(menu.findItem(R.id.menu_refresh));
		refreshMenuUpdater.update(getCurrentPage());

		return super.onPrepareOptionsMenu(menu);
	}

	public void updateMenuItems() {
		if (prevMenuUpdater != null) {
			prevMenuUpdater.update(getCurrentPage());
			nextMenuUpdater.update(getCurrentPage());
			refreshMenuUpdater.update(getCurrentPage());
		}
	}
	
	private int getCurrentPage() {
		return ((TxtApplication) getApplication()).getCurrentPage();
	}

	private int getCurrentPageIndex() {
		return ((TxtApplication) getApplication()).getCurrentPageIndex();
	}

	@Override
	protected void onDestroy() {
		if (task != null) {
			task.cancel(true);
		}
		super.onDestroy();
	}
}
