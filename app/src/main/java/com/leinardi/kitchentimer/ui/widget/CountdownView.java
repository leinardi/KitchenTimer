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

package com.leinardi.kitchentimer.ui.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.LinearLayout;

import com.leinardi.kitchentimer.R;
import com.leinardi.kitchentimer.model.Countdown;
import com.leinardi.kitchentimer.utils.LogUtils;

import java.util.concurrent.TimeUnit;

public class CountdownView extends LinearLayout {
    private static final String TAG = CountdownView.class.getSimpleName();

    final static int TIME_DELAY_MS = 1000;

    public static final int STATE_RUNNING = 1;
    public static final int STATE_STOPPED = 2;
    public static final int STATE_TIMESUP = 3;

    private boolean mVisible;
    private boolean mRunning;

    private Countdown mCountdown;

    private AppCompatTextView mHours, mMinutes, mSeconds;
    private AppCompatTextView mHoursUnit, mMinutesUnit, mSecondsUnit;

    private static final int TICK_WHAT = 2;

    public CountdownView(Context context) {
        this(context, null, 0);
    }

    public CountdownView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountdownView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_countdown, this, true);
        mHours = (AppCompatTextView) findViewById(R.id.hours);
        mHoursUnit = (AppCompatTextView) findViewById(R.id.hours_unit);
        mMinutes = (AppCompatTextView) findViewById(R.id.minutes);
        mMinutesUnit = (AppCompatTextView) findViewById(R.id.minutes_unit);
        mSeconds = (AppCompatTextView) findViewById(R.id.seconds);
        mSecondsUnit = (AppCompatTextView) findViewById(R.id.seconds_unit);
        updateText();
    }

    public Countdown getCountdown() {
        return mCountdown;
    }

    public void setCountdown(Countdown mCountdown) {
        this.mCountdown = mCountdown;
    }

    private synchronized void updateText() {
//        LogUtils.d(TAG, "updateText");

        long absRemainingTime = Math.abs(getTimeLeft());

        long hours = TimeUnit.MILLISECONDS.toHours(absRemainingTime);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(absRemainingTime) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(absRemainingTime) % 60;

        updateTextColor();
        mHours.setText(String.format("%02d", hours));
        mMinutes.setText(String.format("%02d", minutes));
        mSeconds.setText(String.format("%02d", seconds));
    }

    private void updateTextColor() {
        int color;
        int state = getState();
        if (getTimeLeft()< 0 || state == STATE_TIMESUP) {
            color = getResources().getColor(R.color.colorAccent);

        } else if (state == STATE_RUNNING) {
            color = getResources().getColor(R.color.clock_white);

        } else {
            color = getResources().getColor(R.color.clock_gray);

        }
        mHours.setTextColor(color);
        mHoursUnit.setTextColor(color);
        mMinutes.setTextColor(color);
        mMinutesUnit.setTextColor(color);
        mSeconds.setTextColor(color);
        mSecondsUnit.setTextColor(color);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mVisible = false;
        updateRunning();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if ((mVisible && visibility != VISIBLE) || (!mVisible && visibility == VISIBLE)) {
            mVisible = visibility == VISIBLE;
            updateRunning();
        }
    }

    private void updateRunning() {
        LogUtils.d(TAG, "updateRunning");
        boolean running = mVisible;
        if (running != mRunning) {
            if (running) {
                mHandler.sendMessageDelayed(Message.obtain(mHandler, TICK_WHAT), TIME_DELAY_MS);
            } else {
                mHandler.removeMessages(TICK_WHAT);
            }
            mRunning = running;
        }
        updateText();
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message m) {
            if (mRunning && isCountdownObjectValid()) {
//                LogUtils.d(TAG, "handleMessage");
                updateText();
                sendMessageDelayed(Message.obtain(this, TICK_WHAT), TIME_DELAY_MS);
            }
        }
    };

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(CountdownView.class.getName());
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(CountdownView.class.getName());
    }

    private boolean isCountdownObjectValid() {
        return mCountdown != null && mCountdown.isValid();
    }

    private long getTimeLeft() {
        if (isCountdownObjectValid()) {
            return mCountdown.getTimeLeft();
        } else {
            return 0;
        }
    }

    private int getState() {
        if (isCountdownObjectValid()) {
            return mCountdown.getState();
        } else {
            return STATE_STOPPED;
        }
    }
}