package ch.rogerjaeggi.txt;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public enum EPageLinkSetting {
	
	DISABLED(0, R.string.stateDisabled),
	WIFI(1, R.string.stateWifi),
	ALWAYS(2, R.string.stateAlways);

	private static EPageLinkSetting DEFAULT = WIFI;
	
	private int id;
	private int resourceId;
	
	private EPageLinkSetting(int id, int resourceId) {
		this.id = id;
		this.resourceId = resourceId;
	}

	public int getId() {
		return id;
	}

	public String getName(Context context) {
		return context.getString(resourceId);
	}
	
	public static String[] getAllNames(Context context) {
		String result[] = new String[values().length];
		int i = 0;
		for (EPageLinkSetting state : values()) {
			result[i] = context.getString(state.resourceId);
			i++;
		}
		return result;
	}
	
	public static String[] getAllIds() {
		String result[] = new String[values().length];
		int i = 0;
		for (EPageLinkSetting state : values()) {
			result[i] = Integer.toString(state.id);
			i++;
		}
		return result;
	}
	
	public boolean shouldLoadPageLinks(Context context) {
		if (this == DISABLED) {
			return false;
		} else {
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = cm.getActiveNetworkInfo();
			if (info == null || !info.isConnected()) {
				return false;
			} else {
				if (this == WIFI) {
					return info.getType() == ConnectivityManager.TYPE_WIFI;
				} else {
					return true;
				}
			}
		}
	}
	
	public static EPageLinkSetting getById(int id) {
		for (EPageLinkSetting state : values()) {
			if (state.getId() == id) {
				return state;
			}
		}
		return DEFAULT;
	}
}
