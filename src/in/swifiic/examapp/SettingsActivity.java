package in.swifiic.examapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;

public class SettingsActivity extends PreferenceActivity {

	@SuppressWarnings("deprecation")
	// for the call to findPreference()
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar();

		addPreferencesFromResource(R.xml.pref_general);

		Preference button = (Preference) getPreferenceManager().findPreference(
				"resetButton");
		if (button != null) {
			button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference arg0) {
					AlertDialog alertDialog;
					AlertDialog.Builder builder = new AlertDialog.Builder(
							SettingsActivity.this);
					builder.setTitle("Confirm preference reset");
					builder.setMessage("Your preferences will be reset. You have to restart app for changes to take effect.");
					builder.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									SharedPreferences sharedPreferences;
									final String examappPREFERENCES = "examappPrefs";
									final String ROLE = "roleKey";
									sharedPreferences = getSharedPreferences(
											examappPREFERENCES,
											Context.MODE_PRIVATE);
									Editor prefEditor = sharedPreferences
											.edit();
									prefEditor.remove(ROLE);
									prefEditor.commit();
								}
							});
					builder.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
								}
							});
					alertDialog = builder.create();
					alertDialog.show();

					return true;
				}
			});
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		setupSimplePreferencesScreen();
	}

	@SuppressWarnings("deprecation")
	private void setupSimplePreferencesScreen() {
		// Add 'general' preferences.

		// Bind the summaries of EditText to their values.
		bindPreferenceSummaryToValue(findPreference("hub_address"));
		bindPreferenceSummaryToValue(findPreference("my_identity"));
	}

	/**
	 * A preference value change listener that updates the preference's summary
	 * to reflect its new value.
	 */
	private static Preference.OnPreferenceChangeListener prefChangeListener = new Preference.OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object value) {
			if (preference.getKey().equals("hub_address")) {
				String stringValue = value.toString();
				preference.setSummary(stringValue + " - Set from SUTA");
				return true;
			} else if (preference.getKey().equals("my_identity")) {
				String stringValue = value.toString();
				preference.setSummary(stringValue + " - Set from SUTA");
				return true;
			}

			return false;
		}
	};

	/**
	 * Binds a preference's summary to its value. More specifically, when the
	 * preference's value is changed, its summary (line of text below the
	 * preference title) is updated to reflect the value. The summary is also
	 * immediately updated upon calling this method. The exact display format is
	 * dependent on the type of preference.
	 * 
	 * @see #prefChangeListener
	 */
	private static void bindPreferenceSummaryToValue(Preference preference) {
		// Set the listener to watch for value changes.
		preference.setOnPreferenceChangeListener(prefChangeListener);

		// Trigger the listener immediately with the preference's
		// current value.
		prefChangeListener.onPreferenceChange(preference, PreferenceManager
				.getDefaultSharedPreferences(preference.getContext())
				.getString(preference.getKey(), ""));
	}
}
