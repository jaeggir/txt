package ch.rogerjaeggi.txt.ui;

import android.app.Activity;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
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
        
        // clickable link preference
        final CheckBoxPreference checkPref = new CheckBoxPreference(this);
        checkPref.setTitle(R.string.prefsClickableNumbersTitle);
        checkPref.setSummary(R.string.prefsClickableNumbersSummary);
        checkPref.setDefaultValue(Settings.isLinksClickable(this));
        checkPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				boolean linksClickable = (Boolean) newValue;
				if (linksClickable != Settings.isLinksClickable(SettingsActivity.this));
				Settings.setLinksClickable(SettingsActivity.this, linksClickable);
				return true;
			}
		});
        root.addPreference(checkPref);
        
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
