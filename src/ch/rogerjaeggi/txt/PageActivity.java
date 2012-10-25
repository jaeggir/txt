package ch.rogerjaeggi.txt;

import java.util.Calendar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
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
import android.widget.Toast;
import ch.rogerjaeggi.txt.R.anim;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class PageActivity extends SherlockActivity implements OnClickListener {

	private static final int DIALOG_CREDITS = 1;
	private static final int GO_TO_CODE = 77;

	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;

	private static final String BASE_URL = "http://www.teletext.ch/dynpics/";

	AsyncTask<Void, Void, Bitmap> loadPageTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_page);
		setTitle(getTxtApplication().getCurrentChannel().getId() + " - " + getTxtApplication().getCurrentPage());

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
					if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
						return false;
					} else if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
						if (getTxtApplication().getCurrentPage() < 899) {
							runLoadPageTask(null, BASE_URL + getTxtApplication().getCurrentChannel().getId() + "/", getCurrentPage() + 1, 0);
							return true;
						}
					} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
						if (getTxtApplication().getCurrentPage() > 100) {
							runLoadPageTask(null, BASE_URL + getTxtApplication().getCurrentChannel().getId() + "/", getCurrentPage() - 1, 0);
							return true;
						}
					}
				} catch (Exception e) {
					// nothing
				}
				return false;
			}

		});

		View.OnTouchListener gestureListener = new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		};

		View page = findViewById(R.id.page);
		page.setOnClickListener(this);
		page.setOnTouchListener(gestureListener);

		runLoadPageTask(null, BASE_URL + getTxtApplication().getCurrentChannel().getId() + "/", getCurrentPage(), 0);
	}

	@Override
	public void onClick(View v) {
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
		loadPageTask = new LoadPageTask(this, baseUrl, page, subIndex) {

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
			case R.id.menu_credits:
				showDialog(DIALOG_CREDITS);
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
