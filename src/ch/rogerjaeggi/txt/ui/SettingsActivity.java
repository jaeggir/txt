package ch.rogerjaeggi.txt.ui;

import android.app.Activity;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import ch.rogerjaeggi.txt.EChannel;
import ch.rogerjaeggi.txt.R;
import ch.rogerjaeggi.txt.Settings;


public class SettingsActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getString(R.string.menu_settings));

        setPreferenceScreen(createPreferenceHierarchy());
	} 
	
	private PreferenceScreen createPreferenceHierarchy() {
        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);

        final ListPreference listPref = new ListPreference(this);
        listPref.setEntries(EChannel.getAllNames());
        listPref.setEntryValues(EChannel.getAllUrls());
        listPref.setDefaultValue(Settings.getChannel(this).getUrl());
        listPref.setDialogTitle(R.string.prefsChannelDialogTitle);
        listPref.setTitle(R.string.prefsChannelTitle);
        listPref.setSummary(String.format(getString(R.string.prefsChannelSummary), Settings.getChannel(this).getName()));
        listPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
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
        root.addPreference(listPref);
        
        return root;
	}
	
}
