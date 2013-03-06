package ch.rogerjaeggi.txt.ui;

import android.app.Activity;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import ch.rogerjaeggi.txt.EChannel;
import ch.rogerjaeggi.txt.R;
import ch.rogerjaeggi.txt.Settings;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;


public class SettingsActivity extends SherlockPreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getString(R.string.menu_settings));

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
        setPreferenceScreen(createPreferenceHierarchy());
	} 
	
	private PreferenceScreen createPreferenceHierarchy() {
        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);

        // channel preference
        final ListPreference channelPref = new ListPreference(this);
        channelPref.setEntries(EChannel.getAllNames());
        channelPref.setEntryValues(EChannel.getAllUrls());
        channelPref.setDefaultValue(Settings.getChannel(this).getUrl());
        channelPref.setDialogTitle(R.string.prefsChannelDialogTitle);
        channelPref.setTitle(R.string.prefsChannelTitle);
        channelPref.setSummary(String.format(getString(R.string.prefsChannelSummary), Settings.getChannel(this).getName()));
        channelPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				EChannel newChannel =  EChannel.getByUrl((String) newValue);
				if (!newChannel.equals(Settings.getChannel(SettingsActivity.this))) {
					Settings.storeChannel(SettingsActivity.this, newChannel);
					preference.setSummary(String.format(getString(R.string.prefsChannelSummary), newChannel.getName()));
					setResult(Activity.RESULT_OK);
				}
				return true;
			}
		});
        root.addPreference(channelPref);
        
        // preload pages preference
        final ListPreference preloadPagesPref = new ListPreference(this);
        preloadPagesPref.setEntries(new String[] {"0", "1", "2", "3", "4", "5"});
        preloadPagesPref.setEntryValues(new String[] {"0", "1", "2", "3", "4", "5"});
        preloadPagesPref.setDefaultValue(Settings.getPreloadPagesSetting(this));
        preloadPagesPref.setDialogTitle(R.string.prefsPreloadPagesDialogTitle);
        preloadPagesPref.setTitle(R.string.prefsPreloadPagesTitle);
        preloadPagesPref.setSummary(String.format(getString(R.string.prefsPreloadPagesSummary), Settings.getPreloadPagesSetting(this)));
        preloadPagesPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				String preloadPages = (String) newValue;
				if (!preloadPages.equals(Settings.getPreloadPagesSetting(SettingsActivity.this))) {
					Settings.storePreloadPagesSetting(SettingsActivity.this, preloadPages);
					preference.setSummary(String.format(getString(R.string.prefsPreloadPagesSummary), preloadPages));
					setResult(Activity.RESULT_OK);
				}
				return true;
			}
		});
        root.addPreference(preloadPagesPref);
        
        return root;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case android.R.id.home:
	         finish();
	         return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
}
