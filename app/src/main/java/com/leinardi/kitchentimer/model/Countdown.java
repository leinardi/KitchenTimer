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

package com.leinardi.kitchentimer.model;

import android.content.Context;

import com.leinardi.kitchentimer.KitchenTimer;
import com.leinardi.kitchentimer.ui.widget.CountdownView;
import com.leinardi.kitchentimer.utils.LogUtils;
import com.leinardi.kitchentimer.utils.Utils;

import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by leinardi on 17/04/15.
 */
@RealmClass
public class Countdown extends RealmObject {
    public static final String REALM_FILE_NAME = "countdowns.realm";

    // Max timer length is 9 hours + 99 minutes + 99 seconds
    public static final long MAX_TIMER_LENGTH = (9 * 3600 + 99 * 60 + 99) * 1000;

    public static final int STATE_RUNNING = 1;
    public static final int STATE_STOPPED = 2;
    public static final int STATE_TIMESUP = 3;
//    public static final int STATE_DONE = 4;
//    public static final int STATE_RESTART = 5;
//    public static final int STATE_DELETED = 6;

    @PrimaryKey
    private String id;                      // Unique id
    private int timerId;
    private long startTime;                 // With mTimeLeft , used to calculate the correct time
    private long timeLeft;                  // in the timer.
    private long originalLength;            // length set at start of timer and by +1 min after times up
    private long setupLength;               // length set at start of timer
    private int state;
    @Index
    private String label;
    @Ignore
    private CountdownView countdownView;

    private boolean isSwipeLayoutOpen;

    public Countdown() {
        id = UUID.randomUUID().toString();

    }

    public Countdown(String label, long length, int state) {
        this();
        timerId = getNextTimerId();
        this.label = label;
        this.timeLeft = length;
        this.state = state;
        this.startTime = Utils.getTimeNow();
        this.timeLeft = originalLength = setupLength = length;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTimerId() {
        return timerId;
    }

    public void setTimerId(int timerId) {
        this.timerId = timerId;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(long timeLeft) {
        this.timeLeft = timeLeft;
    }

    public long getOriginalLength() {
        return originalLength;
    }

    public void setOriginalLength(long originalLength) {
        this.originalLength = originalLength;
    }

    public long getSetupLength() {
        return setupLength;
    }

    public void setSetupLength(long setupLength) {
        this.setupLength = setupLength;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }


    public boolean isSwipeLayoutOpen() {
        return isSwipeLayoutOpen;
    }

    public void setIsSwipeLayoutOpen(boolean isSwipeLayoutOpen) {
        this.isSwipeLayoutOpen = isSwipeLayoutOpen;
    }

    public CountdownView getCountdownView() {
        return countdownView;
    }

    public void setCountdownView(CountdownView countdownView) {
        this.countdownView = countdownView;
    }

    public static Realm getRealmInstance(Context context) {
        return Realm.getInstance(context, Countdown.REALM_FILE_NAME);
    }

    // Realm allows only default getter and setter or static methods
    public static long updateTimeLeft(Countdown countdown, boolean forceUpdate) {
        if (Countdown.isTicking(countdown) || forceUpdate) {
            long millis = Utils.getTimeNow();
            countdown.setTimeLeft(countdown.getOriginalLength() - (millis - countdown.getStartTime()));
        }
        return countdown.getTimeLeft();
    }

    // Realm allows only default getter and setter or static methods
    public static boolean isTicking(Countdown countdown) {
        return countdown != null && countdown.isValid() && (countdown.getState() == STATE_RUNNING || countdown.getState() == STATE_TIMESUP);
    }

    // Realm allows only default getter and setter or static methods
    public static boolean isInUse(Countdown countdown) {
        return countdown != null && countdown.isValid() && (countdown.getState() == STATE_RUNNING || countdown.getState() == STATE_STOPPED);
    }

    // Realm allows only default getter and setter or static methods
    public static void addTime(Countdown countdown, long time) {
        countdown.setTimeLeft(countdown.getOriginalLength() - (Utils.getTimeNow() - countdown.getStartTime()));
        if (countdown.getTimeLeft() < MAX_TIMER_LENGTH - time) {
            countdown.setOriginalLength(countdown.getOriginalLength() + time);
        }
    }

    private static int getNextTimerId() {
        Realm realm = getRealmInstance(KitchenTimer.getInstance().getApplicationContext());
        final int nextTimerId;
        synchronized (Countdown.class) {
            nextTimerId = (int) (realm.where(Countdown.class).maximumInt("timerId") + 1);
        }
        LogUtils.d("nextTimerId = " + nextTimerId);
        return nextTimerId;
    }
}
