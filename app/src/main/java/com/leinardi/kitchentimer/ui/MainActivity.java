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

package com.leinardi.kitchentimer.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import com.leinardi.kitchentimer.R;
import com.leinardi.kitchentimer.compat.database.DatabaseManager;
import com.leinardi.kitchentimer.model.Countdown;
import com.leinardi.kitchentimer.model.Preset;
import com.leinardi.kitchentimer.receiver.TimerReceiver;
import com.leinardi.kitchentimer.ui.dialog.ChangelogDialogFragment;
import com.leinardi.kitchentimer.ui.dialog.DeleteAllCountdownDialogFragment;
import com.leinardi.kitchentimer.ui.fragment.CountdownListFragment;
import com.leinardi.kitchentimer.ui.fragment.PresetListFragment;
import com.leinardi.kitchentimer.utils.LogUtils;
import com.leinardi.kitchentimer.utils.Utils;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.nispok.snackbar.listeners.EventListener;

import io.realm.Realm;


public class MainActivity extends BaseActivity {
    private String TAG = MainActivity.class.getSimpleName();

    public static final int REQUEST_CODE_COUNTDOWN_DURATION = 1;
    public static final int REQUEST_CODE_SETTINGS = 2;

    private PresetListFragment mPresetListFragment;
    private CountdownListFragment mCountdownListFragment;
    private SlidingPaneLayout mSlidingPaneLayout;
    private boolean mIsSlidingPaneLayoutSlideable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);

        initUi();

        if (checkAndImportOldDatabase()) {
            // TODO show what's new in v2.0
        } else {
            showChangelogOrTutorial();
        }
        Utils.updateSharedPreferencesAppVersion(this);


    }

    private boolean checkAndImportOldDatabase() {
        int previousVersionCode = Utils.getPreviousAppVersionCode(this);
        if (previousVersionCode < 200) {
            DatabaseManager databaseManager = new DatabaseManager(this);
            databaseManager.open();
            boolean success = databaseManager.importPresetFromOldDatabase();
            databaseManager.close();
            if (success) {
                deleteDatabase(DatabaseManager.DB_NAME);
            }
            notifyDataSetChangedPresetList();
            return true;
        } else {
            return false;
        }
    }

    private void showChangelogOrTutorial() {
        int previousVersionCode = Utils.getPreviousAppVersionCode(this);
        int currentVersionCode = Utils.getCurrentAppVersionCode(this);

        if (previousVersionCode == 0) {
            //TODO show tutorial
        } else if (previousVersionCode < currentVersionCode) {
            new ChangelogDialogFragment().show(getFragmentManager(),
                    ChangelogDialogFragment.class.getSimpleName());
        }
    }

    public void notifyDataSetChangedCountdownList() {
        if (mCountdownListFragment != null) {
            mCountdownListFragment.notifyDatasetChanged();
        }
    }

    public void notifyDataSetChangedPresetList() {
        if (mPresetListFragment != null) {
            mPresetListFragment.notifyDatasetChanged();
        }
    }

    private void initUi() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        mPresetListFragment = (PresetListFragment) getSupportFragmentManager().findFragmentById(R.id.preset_list_fragment);
        mCountdownListFragment = (CountdownListFragment) getSupportFragmentManager().findFragmentById(R.id.countdown_list_fragment);

        mSlidingPaneLayout = (SlidingPaneLayout) findViewById(R.id.sliding_pane_layout);

        mSlidingPaneLayout.setParallaxDistance(getResources().getDimensionPixelSize(R.dimen.sliding_pane_width) / 2);
        mSlidingPaneLayout.setShadowResourceLeft(R.drawable.sliding_pane_shadow);
        mSlidingPaneLayout.setSliderFadeColor(getResources().getColor(android.R.color.transparent));

        mSlidingPaneLayout.setPanelSlideListener(new SliderListener());

        mSlidingPaneLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new FirstLayoutListener());
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(TimerReceiver.NOTIF_APP_OPEN, true);
        editor.apply();
        Utils.cancelInUseNotifications(this);
        Utils.cancelTimesUpNotifications(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(TimerReceiver.NOTIF_APP_OPEN, false);
        editor.apply();
        Utils.showInUseNotifications(this);
        Utils.showTimesUpNotifications(this);
    }

    /**
     * This panel slide listener updates the action bar accordingly for each
     * panel state.
     */
    private class SliderListener extends
            SlidingPaneLayout.SimplePanelSlideListener {
        @Override
        public void onPanelOpened(View panel) {
//            setTitle(R.string.preset);
        }

        @Override
        public void onPanelClosed(View panel) {
//            setTitle(R.string.app_name);
        }

        @Override
        public void onPanelSlide(View view, float v) {
            getSupportActionBar().setElevation((1 - v) * 16);
        }
    }

    @Override
    protected void onDestroy() {
//        clearEndedCountdowns();
        super.onDestroy();
    }

    private void clearEndedCountdowns() {
        Realm realm = Countdown.getRealmInstance(this);
        realm.beginTransaction();
        realm.where(Countdown.class)
                .equalTo("state", Countdown.STATE_TIMESUP)
                .findAll()
                .clear();
        realm.commitTransaction();
        realm.close();
//        Utils.cancelInUseNotifications(this);
//        Utils.cancelTimesUpNotifications(this);
    }

    /**
     * This global layout listener is used to fire an event after first layout
     * occurs and then it is removed. This gives us a chance to configure parts
     * of the UI that adapt based on available space after they have had the
     * opportunity to measure and layout.
     */
    private class FirstLayoutListener implements
            ViewTreeObserver.OnGlobalLayoutListener {
        @Override
        public void onGlobalLayout() {
            mIsSlidingPaneLayoutSlideable = mSlidingPaneLayout.isSlideable();
            invalidateOptionsMenu();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mSlidingPaneLayout.getViewTreeObserver()
                        .removeOnGlobalLayoutListener(this);
            } else {
                mSlidingPaneLayout.getViewTreeObserver()
                        .removeGlobalOnLayoutListener(this);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_COUNTDOWN_DURATION) {
            if (resultCode == RESULT_OK) {
                long duration = data.getLongExtra(TimerSetupActivity.EXTRA_TIME, 0);
                if (duration > 0) {
                    String label = data.getStringExtra(TimerSetupActivity.EXTRA_LABEL);
                    if (!Utils.isStringNullOrEmpty(label)) {
                        createCountdownAndSavePreset(duration, label);
                    } else {
                        // TODO
                        createCountdown(duration, null);
                    }
                } else {
                    // TODO Loggare errore duration
                }
            }
        } else if (requestCode == REQUEST_CODE_SETTINGS) {
            if (resultCode == RESULT_OK) {
                notifyDataSetChangedPresetList();
                LogUtils.d(TAG, "REQUEST_CODE_SETTINGS");
            }
        }
    }

    public void createCountdown(long duration, String label) {
        Countdown countdown = new Countdown(label, duration, Countdown.STATE_RUNNING);
        if (Utils.isStringNullOrEmpty(label)) {
            countdown.setLabel(getString(R.string.timer_name_default, countdown.getTimerId()));
        }
        mCountdownListFragment.addCountdownTimer(countdown);
    }

    private void createCountdownAndSavePreset(long duration, String label) {
        createCountdown(duration, label);
        Preset preset = new Preset(label, duration);
        mPresetListFragment.addPreset(preset);
        showCancelActionableToastBar(label, preset);
    }

    private void showCancelActionableToastBar(final String label, final Preset preset) {
        SnackbarManager.show(
                Snackbar.with(MainActivity.this)
                        .text(getString(R.string.preset_saved_message, label))
                        .actionLabel(R.string.undo)
                        .actionColorResource(R.color.colorAccent)
                        .actionListener(new ActionClickListener() {
                            @Override
                            public void onActionClicked(Snackbar snackbar) {
                                mPresetListFragment.removePreset(preset);
                            }
                        })
                        .eventListener(new EventListener() {
                            @Override
                            public void onShow(Snackbar snackbar) {
                                mCountdownListFragment.fabMoveDown(-snackbar.getHeight());
                            }

                            @Override
                            public void onShowByReplace(Snackbar snackbar) {
                            }

                            @Override
                            public void onShown(Snackbar snackbar) {
                            }

                            @Override
                            public void onDismiss(Snackbar snackbar) {
                                mCountdownListFragment.fabMoveDown(0);
                            }

                            @Override
                            public void onDismissByReplace(Snackbar snackbar) {
                            }

                            @Override
                            public void onDismissed(Snackbar snackbar) {
                            }
                        }) // Snackbar's EventListener
                        .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (!mIsSlidingPaneLayoutSlideable) {
            menu.findItem(R.id.action_preset).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_preset:
                if (mSlidingPaneLayout.isOpen()) {
                    mSlidingPaneLayout.closePane();
                } else {
                    mSlidingPaneLayout.openPane();
                }
                return true;
            case R.id.action_delete_all_timers:
                new DeleteAllCountdownDialogFragment().show(getFragmentManager(),
                        DeleteAllCountdownDialogFragment.class.getSimpleName());
                return true;
            case R.id.action_settings:
                startActivityForResult(new Intent(MainActivity.this, SettingsActivity.class), REQUEST_CODE_SETTINGS);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
