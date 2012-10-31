package ch.rogerjaeggi.txt.ui;

import android.app.Activity;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import ch.rogerjaeggi.txt.EChannel;
import ch.rogerjaeggi.txt.EPageLinkSetting;
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
					preference.setSummary(String.format(getString(R.string.prefsChannelSummary), Settings.getChannel(SettingsActivity.this).getName()));
					setResult(Activity.RESULT_OK);
				}
				return true;
			}
		});
        root.addPreference(channelPref);
        
        // clickable link preference
        final ListPreference loadLinksPref = new ListPreference(this);
        loadLinksPref.setEntries(EPageLinkSetting.getAllNames(this));
        loadLinksPref.setEntryValues(EPageLinkSetting.getAllIds());
        loadLinksPref.setDefaultValue(Integer.toString(Settings.getClickableLinkSetting(this).getId()));
        loadLinksPref.setDialogTitle(R.string.prefsClickableNumbersTitle);
        loadLinksPref.setTitle(R.string.prefsClickableNumbersTitle);
        loadLinksPref.setSummary(Settings.getClickableLinkSetting(this).getName(this));
        loadLinksPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				EPageLinkSetting newState =  EPageLinkSetting.getById(Integer.parseInt((String) newValue));
				if (!newState.equals(Settings.getClickableLinkSetting(SettingsActivity.this))) {
					Settings.setClickableLinkSetting(SettingsActivity.this, newState);
					preference.setSummary(Settings.getClickableLinkSetting(SettingsActivity.this).getName(SettingsActivity.this));
					setResult(Activity.RESULT_OK);
				}
				return true;
			}
		});
        root.addPreference(loadLinksPref);
        
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
