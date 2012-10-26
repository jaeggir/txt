package ch.rogerjaeggi.txt;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;

public class GoToActivity extends Activity {

	private EditText digit1;
	private EditText digit2;
	private EditText digit3;

	private final TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable s) {
			if (digit1.getText().length() > 0 && digit2.getText().length() > 0 && digit3.getText().length() > 0) {
				int page = Integer.parseInt(digit1.getText().toString() + digit2.getText().toString() + digit3.getText().toString());
				Intent data = new Intent();
				data.putExtra(Constants.EXTRA_PAGE, page);
				setResult(RESULT_OK, data);
				finish();
			} else if (digit2.getText().length() > 0) {
				if (digit3.getText().length() == 0) {
					digit3.requestFocus();
				} else {
					digit1.requestFocus();
				}
			} else if (digit1.getText().length() > 0) {
				if (digit2.getText().length() == 0) {
					digit2.requestFocus();
				} else {
					digit3.requestFocus();
				}
			} else {
				digit1.requestFocus();
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_goto);

		setTitle("Go To");

		digit1 = (EditText) findViewById(R.id.digit1);
		digit2 = (EditText) findViewById(R.id.digit2);
		digit3 = (EditText) findViewById(R.id.digit3);

		digit1.addTextChangedListener(textWatcher);
		digit1.setSingleLine();
		digit2.addTextChangedListener(textWatcher);
		digit2.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					if (keyCode == KeyEvent.KEYCODE_DEL && digit2.getText().length() == 0) {
						digit1.setText("");
						digit1.requestFocus();
						return true;
					}
				}
				return false;
			}

		});
		digit2.setSingleLine();
		digit3.addTextChangedListener(textWatcher);
		digit3.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					if (keyCode == KeyEvent.KEYCODE_DEL && digit3.getText().length() == 0) {
						digit2.setText("");
						digit2.requestFocus();
						return true;
					}
				}
				return false;
			}

		});
		digit3.setSingleLine();
		digit1.requestFocus();

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			Rect r = new Rect(0, 0, 0, 0);
			getWindow().getDecorView().getHitRect(r);
			boolean intersects = r.contains((int) event.getX(), (int) event.getY());
			if (!intersects) {
				setResult(RESULT_CANCELED);
				finish();
				return true;
			}
		}
		return super.onTouchEvent(event);
	}

}
