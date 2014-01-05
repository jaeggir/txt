package ch.rogerjaeggi.txt.ui;

import android.app.Activity;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.view.MenuItem;

import ch.rogerjaeggi.txt.EChannel;
import ch.rogerjaeggi.txt.R;
import ch.rogerjaeggi.txt.Settings;


public class SettingsActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getString(R.string.menu_settings));

        // TODO action bar for preferences currently not possible with appCompat
        // https://github.com/kolavar/android-support-v4-preferencefragment
		//getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
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
