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

package com.leinardi.kitchentimer.utils;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.leinardi.kitchentimer.receiver.TimerReceiver;

/**
 * Created by leinardi on 19/04/15.
 */
public class Utils {
    private static final String TAG = Utils.class.getSimpleName();
    public static final String PREF_APP_VERSION = "app.version";
    public static final String PREF_CHANGELOG = "changelog";

    public static boolean isStringNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }

    public static String getHumanReadableDuration(long totalMilliseconds) {
        long totalSeconds = totalMilliseconds / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static int getCurrentAppVersionCode(Context context) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            LogUtils.e(TAG, "Package name not found", e);
            return 0;
        }
    }

    public static int getPreviousAppVersionCode(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_CHANGELOG, Activity.MODE_PRIVATE);
        return preferences.getInt(PREF_APP_VERSION, 0);
    }

    public static void updateSharedPreferencesAppVersion(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_CHANGELOG, Activity.MODE_PRIVATE);
        int currentVersionCode = getCurrentAppVersionCode(context);
        if (currentVersionCode > 0) {
            preferences.edit().putInt(PREF_APP_VERSION, currentVersionCode).apply();
        }
    }

    public static long getTimeNow() {
//        return SystemClock.elapsedRealtime();
        return System.currentTimeMillis();
    }

    /**
     * Broadcast a message to show the in-use timers in the notifications
     */
    public static void showInUseNotifications(Context context) {
        Intent timerIntent = new Intent();
        timerIntent.setAction(TimerReceiver.NOTIF_IN_USE_SHOW);
        context.sendBroadcast(timerIntent);
    }

    /**
     * Broadcast a message to cancel the in-use timers in the notifications
     */
    public static void cancelInUseNotifications(Context context) {
        Intent timerIntent = new Intent();
        timerIntent.setAction(TimerReceiver.NOTIF_IN_USE_CANCEL);
        context.sendBroadcast(timerIntent);
    }

    /**
     * Broadcast a message to show the times-up timers in the notifications
     */
    public static void showTimesUpNotifications(Context context) {
        Intent timerIntent = new Intent();
        timerIntent.setAction(TimerReceiver.NOTIF_TIMES_UP_SHOW);
        context.sendBroadcast(timerIntent);
    }

    /**
     * Broadcast a message to cancel the times-up timers in the notifications
     */
    public static void cancelTimesUpNotifications(Context context) {
        Intent timerIntent = new Intent();
        timerIntent.setAction(TimerReceiver.NOTIF_TIMES_UP_CANCEL);
        context.sendBroadcast(timerIntent);
    }

}
