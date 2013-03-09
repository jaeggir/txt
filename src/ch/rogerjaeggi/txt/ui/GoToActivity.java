package ch.rogerjaeggi.txt.ui;

import static ch.rogerjaeggi.txt.Constants.EXTRA_PAGE;
import static ch.rogerjaeggi.txt.Constants.EXTRA_REFRESH;
import static ch.rogerjaeggi.txt.Constants.EXTRA_SUB_PAGE;
import static ch.rogerjaeggi.txt.Constants.DEFAULT_SUB_PAGE;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;
import ch.rogerjaeggi.txt.R;

public class GoToActivity extends Activity {

	private class MyClickListener implements OnClickListener {

		private final int val;

		public MyClickListener(int val) {
			super();
			this.val = val;
		}

		@Override
		public void onClick(View v) {
			if (newPage.length() < 3) {
				newPage.setText(newPage.getText() + Integer.toString(val));
			}
		}

	}

	private TextView newPage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_goto);

		newPage = (TextView) findViewById(R.id.newPage);
		newPage.setSingleLine();
		newPage.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() == 3) {
					int page = Integer.parseInt(s.toString());
					Intent data = new Intent().putExtra(EXTRA_PAGE, page).putExtra(EXTRA_SUB_PAGE, DEFAULT_SUB_PAGE);
					setResult(RESULT_OK, data);
					finish();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
		});

		findViewById(R.id.b0).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (newPage.length() > 0) {
					newPage.setText(newPage.getText() + "0");
				}
			}
		});
		findViewById(R.id.b1).setOnClickListener(new MyClickListener(1));
		findViewById(R.id.b2).setOnClickListener(new MyClickListener(2));
		findViewById(R.id.b3).setOnClickListener(new MyClickListener(3));
		findViewById(R.id.b4).setOnClickListener(new MyClickListener(4));
		findViewById(R.id.b5).setOnClickListener(new MyClickListener(5));
		findViewById(R.id.b6).setOnClickListener(new MyClickListener(6));
		findViewById(R.id.b7).setOnClickListener(new MyClickListener(7));
		findViewById(R.id.b8).setOnClickListener(new MyClickListener(8));
		findViewById(R.id.b9).setOnClickListener(new MyClickListener(9));
		findViewById(R.id.bRefresh).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent data = new Intent();
				data.putExtra(EXTRA_REFRESH, true);
				setResult(RESULT_OK, data);
				finish();
			}
		});
		findViewById(R.id.bDel).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (newPage.length() > 0) {
					newPage.setText(newPage.getText().subSequence(0, newPage.getText().length() - 1));
				}
			}
		});

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// to handle clicks outside the dialog on android < 3.0.
		if (event.getAction() == MotionEvent.ACTION_UP) {
			Rect r = new Rect(0, 0, 0, 0);
			getWindow().getDecorView().getHitRect(r);
			boolean intersects = r.contains((int) event.getX(), (int) event.getY());
			if (!intersects && !isFinishing()) {
				setResult(RESULT_CANCELED);
				finish();
				return true;
			}
		}
		return super.onTouchEvent(event);
	}

}
