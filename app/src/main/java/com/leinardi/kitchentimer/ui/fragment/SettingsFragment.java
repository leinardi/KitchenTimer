/*
 * Kitchen Timer
 * Copyright (C) 2015 Roberto Leinardi
 *
 * This program is free software: you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this
 * program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.leinardi.kitchentimer.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.widget.Toast;

import com.leinardi.kitchentimer.R;
import com.leinardi.kitchentimer.ui.dialog.ChangelogDialogFragment;
import com.leinardi.kitchentimer.ui.dialog.DeleteAllPresetsDialogFragment;
import com.leinardi.kitchentimer.ui.dialog.OpenSourceLicensesDialogFragment;
import com.leinardi.kitchentimer.utils.PresetImportExportManager;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;

/**
 * Created by leinardi on 18/04/15.
 */
public class SettingsFragment extends PreferenceFragment {
    private static final String VERSION_UNAVAILABLE = "-";
    private static final String REPORT_A_BUG_URL = "https://github.com/leinardi/KitchenTimer/issues";

    long[] mHits = new long[3];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        setupSimplePreferencesScreen();
    }


    private void setupSimplePreferencesScreen() {
        // Add 'general' preferences.
        addPreferencesFromResource(R.xml.settings);

        // Get app version
        PackageManager pm = getActivity().getPackageManager();
        String packageName = getActivity().getPackageName();
        String versionName;
        try {
            PackageInfo info = pm.getPackageInfo(packageName, 0);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            versionName = VERSION_UNAVAILABLE;
        }

        Preference versionPreference = findPreference(getString(R.string.pref_version_key));
        versionPreference.setSummary(versionName);
        versionPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
                    // TODO add Easter egg
                    Toast.makeText(getActivity(), "No Easter egg yet, for now :-)", Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });

        findPreference(getString(R.string.pref_report_a_bug_key))
                .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        openBrowser(REPORT_A_BUG_URL);
                        return true;
                    }
                });

        findPreference(getString(R.string.pref_changelog_key))
                .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        new ChangelogDialogFragment().show(getFragmentManager(),
                                ChangelogDialogFragment.class.getSimpleName());
                        return true;
                    }
                });

        findPreference(getString(R.string.pref_licenses_key))
                .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        new OpenSourceLicensesDialogFragment().show(getFragmentManager(),
                                OpenSourceLicensesDialogFragment.class.getSimpleName());
                        return true;
                    }
                });

        final Preference ringtonePreference = findPreference(getString(R.string.pref_notification_ringtone_key));

        bindPreferenceSummaryToValue(ringtonePreference);

        findPreference(getString(R.string.pref_backup_key))
                .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        Context context = getActivity();
                        boolean success = PresetImportExportManager.exportToSD(context);
                        if (success) {
                            SnackbarManager.show(Snackbar.with(getActivity())
                                            .text(R.string.presets_export_success)
                            );
                        } else {
                            SnackbarManager.show(Snackbar.with(getActivity())
                                            .text(R.string.presets_export_error)
                                            .type(SnackbarType.MULTI_LINE)
                                            .textColorResource(R.color.red)
                                            .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                            );
                        }
                        return true;
                    }
                });

        findPreference(getString(R.string.pref_import_key))
                .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        boolean success = PresetImportExportManager.importFromSD(getActivity());
                        if (success) {
                            getActivity().setResult(Activity.RESULT_OK);
                            SnackbarManager.show(Snackbar.with(getActivity())
                                            .text(R.string.presets_import_success)
                            );
                        } else {
                            SnackbarManager.show(Snackbar.with(getActivity())
                                            .text(R.string.presets_import_error)
                                            .type(SnackbarType.MULTI_LINE)
                                            .textColorResource(R.color.red)
                                            .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                            );
                        }
                        return true;
                    }
                });

        findPreference(getString(R.string.pref_delete_all_key))
                .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        new DeleteAllPresetsDialogFragment().show(getFragmentManager(),
                                DeleteAllPresetsDialogFragment.class.getSimpleName());
                        return true;
                    }
                });
    }

    private void openBrowser(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }


    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary("pref_ringtone_silent");

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };
}