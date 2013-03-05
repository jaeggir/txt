package ch.rogerjaeggi.txt.ui;

import java.util.Calendar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import ch.rogerjaeggi.txt.Constants;
import ch.rogerjaeggi.txt.EPageLinkSetting;
import ch.rogerjaeggi.txt.Page;
import ch.rogerjaeggi.txt.R;
import ch.rogerjaeggi.txt.R.anim;
import ch.rogerjaeggi.txt.Settings;
import ch.rogerjaeggi.txt.TxtApplication;
import ch.rogerjaeggi.txt.loader.IRequestListener;
import ch.rogerjaeggi.txt.loader.PageRequest;
import ch.rogerjaeggi.txt.loader.RequestManager;
import ch.rogerjaeggi.txt.loader.TouchableArea;
import ch.rogerjaeggi.txt.loader.TxtResult;
import ch.rogerjaeggi.txt.loader.cache.TxtCache;
import ch.rogerjaeggi.txt.loader.cache.TxtKey;
import ch.rogerjaeggi.txt.menu.NextMenuUpdater;
import ch.rogerjaeggi.txt.menu.PrevMenuUpdater;
import ch.rogerjaeggi.txt.menu.RefreshMenuUpdater;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class PageActivity extends SherlockActivity implements OnClickListener, IRequestListener {

	private TxtKey requestedPage;

	private static final int DIALOG_CREDITS = 1;
	private static final int DIALOG_LOADING = 2;

	private static final int GO_TO_CODE = 77;
	private static final int GO_TO_SETTINGS = 78;

	private static final int SWIPE_THRESHOLD_VELOCITY = 200;

	private int swipeMinDistance;

	private PrevMenuUpdater prevMenuUpdater;
	private NextMenuUpdater nextMenuUpdater;
	private RefreshMenuUpdater refreshMenuUpdater;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_page);

		setTitle();
	}

	@Override
	protected void onResume() {
		super.onResume();

		getRequestManager().setListener(this);

		if (requestedPage != null) {
			requestPage(requestedPage.getPage(), requestedPage.getSubPage(), false);
		} else {
			requestPage(getCurrentPage(), getCurrentPageIndex(), false);
		}

	}

	@Override
	protected void onPause() {
		getRequestManager().removeListener();

		super.onPause();
	}

	private void setTitle() {
		int screenLayout = getResources().getConfiguration().screenLayout;
		boolean largeScreen = (screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE;
		boolean xLargeScreen = (screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4; // xLarge, not available in api level 8
		if (largeScreen || xLargeScreen || isLandscapeMode()) {
			setTitle(Settings.getChannel(this).getName() + " - " + getCurrentPage());
		} else {
			setTitle(Integer.toString(getCurrentPage()));
		}
	}

	private boolean isLandscapeMode() {
		return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == GO_TO_CODE && RESULT_OK == resultCode) {
			boolean refresh = data.getBooleanExtra(Constants.EXTRA_REFRESH, false);
			requestPage(data.getIntExtra(Constants.EXTRA_PAGE, 100), 0, refresh);
		} else if (requestCode == GO_TO_SETTINGS && RESULT_OK == resultCode) {
			requestPage(100, 0, false);
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	private void requestPage(final int page, final int subIndex, boolean forceRefresh) {
		startRefreshIndicators();

		//		new Handler().postDelayed(new Runnable() {
		//
		//			@Override
		//			public void run() {
		//				if (getCurrentPage() != page && getCurrentPageIndex() != subIndex) {
		//					showDialog(DIALOG_LOADING);
		//				}
		//			}
		//			
		//		}, 150);

		TxtKey key = new TxtKey(Settings.getChannel(PageActivity.this), page, subIndex);
		boolean loadPageLinks = Settings.getClickableLinkSetting(this).shouldLoadPageLinks(this);
		PageRequest request = new PageRequest(key, loadPageLinks, forceRefresh);

		if (forceRefresh || !TxtCache.contains(key)) {
			showDialog(DIALOG_LOADING);
		}
		requestedPage = key;
		getRequestManager().requestPage(request);
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
				requestPage(getCurrentPage(), getCurrentPageIndex(), true);
				return true;
			case R.id.menu_backwards:
				item.setEnabled(false);
				requestPage(getCurrentPage() - 1, 0, false);
				return true;
			case R.id.menu_forewards:
				item.setEnabled(false);
				requestPage(getCurrentPage() + 1, 0, false);
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
				((TextView) dialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
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
			case DIALOG_LOADING:
				return MyProgressDialog.show(this, null, "");
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

	private int getCurrentPage() {
		return ((TxtApplication) getApplication()).getCurrentPage();
	}

	private int getCurrentPageIndex() {
		return ((TxtApplication) getApplication()).getCurrentPageIndex();
	}

	private RequestManager getRequestManager() {
		return ((TxtApplication) getApplication()).getRequestManager();
	}

	private void updateMenuItems() {
		if (prevMenuUpdater != null) {
			prevMenuUpdater.update(getCurrentPage());
			nextMenuUpdater.update(getCurrentPage());
			refreshMenuUpdater.update(getCurrentPage());
		}
	}

	private void cancelRefreshIndicators() {
		if (refreshMenuUpdater != null && refreshMenuUpdater.getMenuItem().getActionView() != null) {
			refreshMenuUpdater.getMenuItem().getActionView().clearAnimation();
			refreshMenuUpdater.getMenuItem().setActionView(null);
		}

		removeDialog(DIALOG_LOADING);
	}

	private void startRefreshIndicators() {
		if (refreshMenuUpdater != null) {
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			ImageView iv = (ImageView) inflater.inflate(R.layout.refresh_action_view, null);

			Animation rotation = AnimationUtils.loadAnimation(this, anim.refresh_animation);
			rotation.setRepeatCount(Animation.INFINITE);
			iv.startAnimation(rotation);

			refreshMenuUpdater.getMenuItem().setActionView(iv);
		}
	}

	@Override
	public void onClick(View v) {
		// empty implementation
	}

	private int getSwipeMinDistance() {
		if (swipeMinDistance == 0) {
			swipeMinDistance = ViewConfiguration.get(this).getScaledTouchSlop() * 2;
		}
		return swipeMinDistance;
	}

	@Override
	public void onBackPressed() {
		((TxtApplication) getApplication()).popHistory(); // current page
		Page prevPage = ((TxtApplication) getApplication()).popHistory(); // previous page
		if (prevPage == null) {
			super.onBackPressed();
		} else {
			requestPage(prevPage.getPage(), prevPage.getSubPage(), false);
		}
	}

	@Override
	public void notifyLoaded(final TxtResult result) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				final ImageView image = (ImageView) findViewById(R.id.page);
				TxtApplication app = (TxtApplication) getApplication();
				if (result.getBitmap() == null) {
					View errorText = findViewById(R.id.errorText);
//					if (!errorText.isShown()) {
						image.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.page_does_not_exists));
						errorText.setVisibility(View.VISIBLE);
						if (result.getBitmap() != null) {
							((TextView) findViewById(R.id.errorText)).setText(R.string.errorConnectionProblem);
						} else {
							((TextView) findViewById(R.id.errorText)).setText(String.format(getString(R.string.errorPageNotFound), result.getKey().getPage()));
						}
//					} else {
//						// ignore
//					}
				} else {
					findViewById(R.id.errorText).setVisibility(View.GONE);
					image.setImageBitmap(result.getBitmap());
					app.setCurrentPageIndex(result.getKey().getSubPage());
				}
				image.setOnClickListener(PageActivity.this);
				image.setScaleType(isLandscapeMode() ? ScaleType.FIT_CENTER : ScaleType.FIT_XY);
				image.setOnTouchListener(new OnTouchListener() {

					// Gesture detection
					final GestureDetector gestureDetector = new GestureDetector(PageActivity.this, new SimpleOnGestureListener() {

						@Override
						public boolean onSingleTapUp(MotionEvent event) {
							if (result.getBitmap() == null) {
								handleSimpleClick();
							} else {
								if (Settings.getClickableLinkSetting(PageActivity.this) != EPageLinkSetting.DISABLED) {
									int x = 0;
									int y = 0;
									if (isLandscapeMode()) {
										float scale = (float) image.getHeight() / result.getBitmap().getHeight();
										Display display = getWindowManager().getDefaultDisplay();
										int displayWidth = display.getWidth();
										x = (int) ((event.getX() - (displayWidth - scale * result.getBitmap().getWidth()) / 2) / scale);
										y = (int) (event.getY() / scale);
									} else {
										Rect r = image.getDrawable().getBounds();
										float scaleX = (float) (r.right - r.left) / result.getBitmap().getWidth();
										float scaleY = (float) (r.bottom - r.top) / result.getBitmap().getHeight();
										x = (int) (event.getX() / scaleX);
										y = (int) (event.getY() / scaleY);
									}
									TouchableArea area = result.intersects(new Rect(x - 5, y - 5, x + 5, y + 5));
									if (area != null) {
										requestPage(area.getTarget(), 0, false);
									} else {
										handleSimpleClick();
									}
								} else {
									handleSimpleClick();
								}
							}
							return true;
						}

						private void handleSimpleClick() {
							Intent intent = new Intent(PageActivity.this, GoToActivity.class);
							startActivityForResult(intent, GO_TO_CODE);
						}

						@Override
						public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
							try {
								if (e1.getX() - e2.getX() > getSwipeMinDistance() && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
									int nextPage = getCurrentPage() < 899 ? getCurrentPage() + 1 : 100;
									requestPage(nextPage, 0, false);
									return true;
								} else if (e2.getX() - e1.getX() > getSwipeMinDistance() && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
									if (getCurrentPage() > 100) {
										requestPage(getCurrentPage() - 1, 0, false);
										return true;
									}
								} else if (e1.getY() - e2.getY() > getSwipeMinDistance() && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
									if (getCurrentPageIndex() > 0 && getCurrentPage() != 100) {
										requestPage(getCurrentPage(), getCurrentPageIndex() - 1, false);
										return true;
									}
								} else if (e2.getY() - e1.getY() > getSwipeMinDistance() && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
									requestPage(getCurrentPage(), getCurrentPageIndex() + 1, false);
									return true;
								}
							} catch (Exception e) {
								// nothing
							}
							return false;
						}

					});

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						return gestureDetector.onTouchEvent(event);
					}

				});
				app.setCurrentPage(result.getKey().getPage());
				image.setVisibility(View.VISIBLE);
				image.invalidate();

				setTitle();

				updateMenuItems();
				cancelRefreshIndicators();
				((TxtApplication) getApplication()).pushHistory(Settings.getChannel(PageActivity.this), result.getKey().getPage(), result.getKey().getSubPage());

				requestedPage = null;
			}

		});
	}
}
