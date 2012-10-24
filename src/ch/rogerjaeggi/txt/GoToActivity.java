package ch.rogerjaeggi.txt;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;



public class GoToActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_goto);
		
		setTitle("Go To");
		
		final EditText digit1 = (EditText) findViewById(R.id.digit1);
		final EditText digit2 = (EditText) findViewById(R.id.digit2);
		final EditText digit3 = (EditText) findViewById(R.id.digit3);
		
		digit1.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() == 1) {
					digit2.requestFocus();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
		});
		digit1.setSingleLine();
		digit2.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() == 1) {
					digit3.requestFocus();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
		});
		digit2.setSingleLine();

		digit3.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				if (digit1.length() == 1 && digit2.length() == 1 && s.length() == 1) {
					int page = Integer.parseInt(digit1.getText().toString() + digit2.getText().toString() + digit3.getText().toString());
					Intent data = new Intent();
					data.putExtra(Constants.EXTRA_PAGE, page);
					setResult(RESULT_OK, data);
					finish();
				} else if (digit1.length() != 1) {
					digit1.requestFocus();
				} else {
					digit2.requestFocus();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
		});
		digit3.setSingleLine();
		
		digit1.requestFocus();
		
	}
	
}
