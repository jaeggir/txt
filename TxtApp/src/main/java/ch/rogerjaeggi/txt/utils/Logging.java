package ch.rogerjaeggi.txt.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import ch.rogerjaeggi.txt.BuildConfig;

/**
 * Simple log wrapper for Android, delegates calls to android.util.Log. 
 * You must call init(Context) before the class is used the first time, e.g.
 * in your main application class.
 *  
 * @author Roger Jaeggi
 */
public class Logging {
	
	private static String appName = null;
	
	public static void init(Context ctx) {
		final PackageManager pm = ctx.getPackageManager();
		try {
			ApplicationInfo ai = pm.getApplicationInfo(ctx.getPackageName(), 0);
			appName = ai == null ? "" : pm.getApplicationLabel(ai) + ".";
		} catch (NameNotFoundException e) {
			if (BuildConfig.DEBUG) {
				Log.d("Logging", "Debugging is not properly initialized", e);
			}
		}
	}
	
	private static String getAppName() {
		if (appName == null) {
			if (BuildConfig.DEBUG) {
				Log.d("Logging", "Debugging is not properly initialized, call Logging.init(Context)");
			}
			appName = "";
		}
		return appName;
	}
	
	private static String getTag(Object o) {
		return getAppName() + getClassName(o.getClass());
	}

	private static String getClassName(Class<?> cls) {
		if (cls.getSimpleName().length() == 0) {
			String name = cls.toString();
			if (name == null) {
				return "";
			} else {
				int start = name.lastIndexOf(".");
				if (start == -1) {
					return name;
				} else {
					return name.substring(start + 1);
				}
			}
		} else {
			return cls.getSimpleName();
		}
	}

	public static void d(Object o, String message) {
		if (BuildConfig.DEBUG) {
			Log.d(getTag(o), message);
		}
	}
	
	public static void d(Object o, String message, Throwable t) {
		if (BuildConfig.DEBUG) {
			Log.d(getTag(o), message, t);
		}
	}
	
	public static void i(Object o, String message) {
		Log.i(getTag(o), message);
	}
	
	public static void i(Object o, String message, Throwable t) {
		Log.i(getTag(o), message, t);
	}
	
	public static void e(Object o, String message) {
		Log.e(getTag(o), message);
	}
	
	public static void e(Object o, String message, Throwable t) {
		Log.e(getTag(o), message, t);
	}
	
	public static void v(Object o, String message) {
		Log.v(getTag(o), message);
	}
	
	public static void v(Object o, String message, Throwable t) {
		Log.v(getTag(o), message, t);
	}
	
	public static void w(Object o, String message) {
		Log.w(getTag(o), message);
	}
	
	public static void w(Object o, String message, Throwable t) {
		Log.w(getTag(o), message, t);
	}
}
