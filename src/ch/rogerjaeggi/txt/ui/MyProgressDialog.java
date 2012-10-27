package ch.rogerjaeggi.txt.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;

// http://stackoverflow.com/questions/3225889/how-to-center-progress-indicator-in-progressdialog-easily-when-no-title-text-pa
public class MyProgressDialog extends Dialog {
	
    public static MyProgressDialog show(Context context, CharSequence title, CharSequence message) {
        return show(context, title, message, false);
    }

    public static MyProgressDialog show(Context context, CharSequence title, CharSequence message, boolean indeterminate) {
        return show(context, title, message, indeterminate, false, null);
    }

    public static MyProgressDialog show(Context context, CharSequence title, CharSequence message, boolean indeterminate, boolean cancelable) {
        return show(context, title, message, indeterminate, cancelable, null);
    }

    /**
	 * @param message  
     * @param indeterminate 
	 */
    public static MyProgressDialog show(Context context, CharSequence title, CharSequence message, boolean indeterminate, boolean cancelable, OnCancelListener cancelListener) {
        MyProgressDialog dialog = new MyProgressDialog(context);
        dialog.setTitle(title);
        dialog.setCancelable(cancelable);
        dialog.setOnCancelListener(cancelListener);
        /* The next line will add the ProgressBar to the dialog. */
        dialog.addContentView(new ProgressBar(context, null, android.R.attr.progressBarStyleLarge), new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        dialog.show();

        return dialog;
    }

    public MyProgressDialog(Context context) {
        super(context, ch.rogerjaeggi.txt.R.style.MyProgressDialog);
    }
}