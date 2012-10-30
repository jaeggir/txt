package ch.rogerjaeggi.txt;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class Settings {

	private static final String PREFS = "txtPrefs";
	private static final String KEY_CHANNEL = "key.channel";
	private static final String KEY_CLICKABLE_LINKS = "key.clickableLinks";
			
	private static Method sApplyMethod = findApplyMethod();
	
	private Settings() { }
	
	public static EChannel getChannel(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
		int id = prefs.getInt(KEY_CHANNEL, 0);
		return EChannel.getById(id);
	}
	
	public static void storeChannel(Context context, EChannel channel) {
		Editor editor = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit();
		editor.putInt(KEY_CHANNEL, channel.getId());
        apply(editor);	
	}

	public static boolean isLinksClickable(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
		return prefs.getBoolean(KEY_CLICKABLE_LINKS, true);
	}
	
	public static void setLinksClickable(Context context, boolean linksClickable) {
		Editor editor = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit();
		editor.putBoolean(KEY_CLICKABLE_LINKS, linksClickable);
        apply(editor);
	}
	
	private static Method findApplyMethod() {
		try {
			Class<?> cls = SharedPreferences.Editor.class;
			return cls.getMethod("apply");
		} catch (NoSuchMethodException unused) {
			// fall through, apply is not available before API level 9
		}
		return null;
	}

	private static void apply(final SharedPreferences.Editor editor) {
        if (sApplyMethod != null) {
            try {
                sApplyMethod.invoke(editor);
                return;
            } catch (IllegalArgumentException e) {
    			// fall through, apply is not available before API level 9
			} catch (InvocationTargetException e) {
				// fall through, apply is not available before API level 9
	        } catch (IllegalAccessException unused) {
				// fall through, apply is not available before API level 9
	        }
        }
        // fallback
        new Thread(new Runnable() {

			@Override
			public void run() {
				editor.commit();
			}
        	
        }).start();
    }
}
