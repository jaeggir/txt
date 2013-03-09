package ch.rogerjaeggi.txt.ui;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.SCREENLAYOUT_SIZE_LARGE;
import static android.content.res.Configuration.SCREENLAYOUT_SIZE_MASK;
import static ch.rogerjaeggi.txt.Constants.EXTRA_PAGE;
import static ch.rogerjaeggi.txt.Constants.EXTRA_REFRESH;
import static ch.rogerjaeggi.txt.Constants.EXTRA_SUB_PAGE;
import static ch.rogerjaeggi.txt.Constants.GO_TO_CODE;
import static ch.rogerjaeggi.txt.loader.PageKey.DEFAULT_PAGE;
import static ch.rogerjaeggi.txt.loader.PageKey.DEFAULT_SUB_PAGE;
import static ch.rogerjaeggi.txt.loader.PageKeyFactory.getNextPageKey;
import static ch.rogerjaeggi.txt.loader.PageKeyFactory.getPageKey;
import static ch.rogerjaeggi.txt.loader.PageKeyFactory.getPreviousPageKey;

import java.util.Calendar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import ch.rogerjaeggi.txt.EChannel;
import ch.rogerjaeggi.txt.R;
import ch.rogerjaeggi.txt.R.anim;
import ch.rogerjaeggi.txt.Settings;
import ch.rogerjaeggi.txt.TxtApplication;
import ch.rogerjaeggi.txt.loader.EErrorType;
import ch.rogerjaeggi.txt.loader.IRequestListener;
import ch.rogerjaeggi.txt.loader.PageInfo;
import ch.rogerjaeggi.txt.loader.PageKey;
import ch.rogerjaeggi.txt.loader.PageKeyFactory;
import ch.rogerjaeggi.txt.loader.RequestManager;
import ch.rogerjaeggi.txt.loader.TouchableArea;
import ch.rogerjaeggi.txt.loader.TxtCache;
import ch.rogerjaeggi.txt.loader.TxtResult;
import ch.rogerjaeggi.txt.menu.NextMenuUpdater;
import ch.rogerjaeggi.txt.menu.PrevMenuUpdater;
import ch.rogerjaeggi.txt.menu.RefreshMenuUpdater;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class PageActivity extends SherlockActivity implements OnClickListener, IRequestListener {

	private static final int DIALOG_CREDITS = 1;
	private static final int DIALOG_LOADING = 2;

	private static final int GO_TO_SETTINGS = 78;

	private int swipeMinDistance;

	private PrevMenuUpdater prevMenuUpdater;
	private NextMenuUpdater nextMenuUpdater;
	private RefreshMenuUpdater refreshMenuUpdater;
	
	private boolean progressDialogOnScreen;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_page);

		updateTitle();

		if (savedInstanceState != null) {
			progressDialogOnScreen = savedInstanceState.getBoolean("progress", false);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		getRequestManager().setListener(this);
		
		if (!getRequestManager().hasRequestInProgress()) {
			requestPage(getPageKey(getCurrentPageInfo(), true));
		}
	}

	@Override
	protected void onStop() {
		getRequestManager().removeListener();

		super.onStop();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putBoolean("progress", progressDialogOnScreen);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (RESULT_OK == resultCode) {

			EChannel channel = Settings.getChannel(this);
			if (GO_TO_CODE == requestCode) {
				if (data.hasExtra(EXTRA_REFRESH)) {
					requestPage(getPageKey(getCurrentPageInfo(), true));
				} else {
					int page = data.getIntExtra(EXTRA_PAGE, DEFAULT_PAGE);
					int subPage = data.getIntExtra(EXTRA_SUB_PAGE, DEFAULT_SUB_PAGE);
					requestPage(new PageKey(channel, page, subPage));
				}
			} else if (GO_TO_SETTINGS == requestCode) {
				getTxtApplication().resetPageInfo();
				requestPage(PageKeyFactory.getDefault(channel));
			} else {
				super.onActivityResult(requestCode, resultCode, data);
			}

		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		getSupportMenuInflater().inflate(R.menu.activity_page, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		PageInfo pageInfo = getCurrentPageInfo();

		switch (item.getItemId()) {
			case R.id.menu_refresh:
				item.setEnabled(false);
				requestPage(getPageKey(pageInfo, true));
				return true;
			case R.id.menu_backwards:
				item.setEnabled(false);
				requestPage(getPreviousPageKey(pageInfo));
				return true;
			case R.id.menu_forewards:
				item.setEnabled(false);
				requestPage(getNextPageKey(pageInfo));
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
			case DIALOG_LOADING:
				progressDialogOnScreen = true;
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
		PageInfo currentPageInfo = getCurrentPageInfo();

		prevMenuUpdater = new PrevMenuUpdater(menu.findItem(R.id.menu_backwards));
		prevMenuUpdater.update(currentPageInfo);

		nextMenuUpdater = new NextMenuUpdater(menu.findItem(R.id.menu_forewards));
		nextMenuUpdater.update(currentPageInfo);

		refreshMenuUpdater = new RefreshMenuUpdater(menu.findItem(R.id.menu_refresh));
		refreshMenuUpdater.update(currentPageInfo);

		return super.onPrepareOptionsMenu(menu);
	}

	private void updateTitle() {
		int screenLayout = getResources().getConfiguration().screenLayout;
		boolean largeScreen = (screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_LARGE;
		boolean xLargeScreen = (screenLayout & SCREENLAYOUT_SIZE_MASK) == 4; // xLarge, not available in api level 8

		PageInfo pageInfo = getCurrentPageInfo();

		if (largeScreen || xLargeScreen || isLandscapeMode()) {
			setTitle(pageInfo.getChannel().getName() + " - " + pageInfo.getPage());
		} else {
			setTitle(Integer.toString(pageInfo.getPage()));
		}
	}

	private boolean isLandscapeMode() {
		return getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE;
	}

	public void requestPage(PageKey key) {
		startRefreshIndicators();

		if (key.isForceRefresh()) {
			TxtCache.remove(key);
		}
		
		if (!progressDialogOnScreen && !TxtCache.contains(key)) {
			// only show dialog if we have to fetch the page from SwissTXT
			showDialog(DIALOG_LOADING);
		}

		getRequestManager().requestPage(key);
	}

	private PageInfo getCurrentPageInfo() {
		return getTxtApplication().getCurrentPageInfo();
	}

	private RequestManager getRequestManager() {
		return getTxtApplication().getRequestManager();
	}

	private void updateMenuItems() {
		if (prevMenuUpdater != null) {
			prevMenuUpdater.update(getCurrentPageInfo());
			nextMenuUpdater.update(getCurrentPageInfo());
			refreshMenuUpdater.update(getCurrentPageInfo());
		}
	}

	private void cancelRefreshIndicators() {
		if (refreshMenuUpdater != null && refreshMenuUpdater.getMenuItem().getActionView() != null) {
			refreshMenuUpdater.getMenuItem().getActionView().clearAnimation();
			refreshMenuUpdater.getMenuItem().setActionView(null);
		}

		removeDialog(DIALOG_LOADING);
		progressDialogOnScreen = false;
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

	@Override
	public void onBackPressed() {

		getTxtApplication().popHistory(); // remove current page

		PageKey prevPage = getTxtApplication().popHistory();
		if (prevPage == null) {
			super.onBackPressed();
		} else {
			requestPage(prevPage);
		}
	}

	private TxtApplication getTxtApplication() {
		return (TxtApplication) getApplication();
	}

	private int getSwipeMinDistance() {
		if (swipeMinDistance == 0) {
			swipeMinDistance = ViewConfiguration.get(this).getScaledTouchSlop() * 2;
		}
		return swipeMinDistance;
	}

	@Override
	public void notifyPageLoaded(final TxtResult result) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				findViewById(R.id.errorText).setVisibility(View.GONE);

				final ImageView image = (ImageView) findViewById(R.id.page);
				image.setScaleType(isLandscapeMode() ? ScaleType.FIT_CENTER : ScaleType.FIT_XY);
				image.setImageBitmap(result.getBitmap());
				image.setOnClickListener(PageActivity.this);
				image.setOnTouchListener(new PageTouchListener(PageActivity.this, new AbstractPageGestureListener(PageActivity.this, result.getPageInfo(), getSwipeMinDistance()) {

					@Override
					public boolean onSingleTapConfirmed(MotionEvent event) {
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
							requestPage(new PageKey(result.getPageInfo().getChannel(), area.getPage(), area.getSubPage()));
						} else {
							handleSimpleClick();
						}
						return true;
					}
				}));
				image.setVisibility(View.VISIBLE);
				image.invalidate();
				
				getTxtApplication().setCurrentPageInfo(result.getPageInfo());

				updateTitle();
				updateMenuItems();
				cancelRefreshIndicators();
				getTxtApplication().pushHistory(getPageKey(result.getPageInfo(), false));
			}

		});
	}

	@Override
	public void notifyPageLoadFailed(final PageInfo pageInfo, final EErrorType errorType) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				ImageView image = (ImageView) findViewById(R.id.page);
				image.setScaleType(isLandscapeMode() ? ScaleType.FIT_CENTER : ScaleType.FIT_XY);
				image.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.page_does_not_exists));
				image.setOnClickListener(PageActivity.this);
				image.setOnTouchListener(new PageTouchListener(PageActivity.this, new AbstractPageGestureListener(PageActivity.this, pageInfo, getSwipeMinDistance()) {

					@Override
					public boolean onSingleTapConfirmed(MotionEvent event) {
						handleSimpleClick();
						return true;
					}
				}));
				image.setVisibility(View.VISIBLE);
				image.invalidate();

				TextView errorText = (TextView) findViewById(R.id.errorText);
				errorText.setVisibility(View.VISIBLE);
				switch (errorType) {
					case CONNECTION_PROBLEM:
						// TODO show toast if we are already showing the same page and only the refresh failed.
						errorText.setText(R.string.errorConnectionProblem);
						break;
					case PAGE_NOT_FOUND:
						errorText.setText(String.format(getString(R.string.errorPageNotFound), pageInfo.getPage()));
						break;
					case OTHER_PROBLEM:
						errorText.setText(getString(R.string.errorOther));
						break;
				}
				
				getTxtApplication().setCurrentPageInfo(pageInfo);
				updateTitle();
				updateMenuItems();
				cancelRefreshIndicators();
				getTxtApplication().pushHistory(getPageKey(pageInfo, false));
			}
		});
	}
}
