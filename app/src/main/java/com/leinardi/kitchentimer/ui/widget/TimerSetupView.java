/*
 * Copyright (C) 2012 The Android Open Source Project
 * Copyright (C) 2015 Roberto Leinardi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.leinardi.kitchentimer.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leinardi.kitchentimer.R;
import com.leinardi.kitchentimer.utils.AnimatorUtils;

public class TimerSetupView extends LinearLayout implements Button.OnClickListener,
        Button.OnLongClickListener {

    protected int mInputSize = 5;

    protected EditText mTimerLabel;
    protected final Button mNumbers[] = new Button[10];
    protected int mInput[] = new int[mInputSize];
    protected int mInputPointer = -1;
    protected Button mLeft, mRight;
    protected ImageButton mStart;
    protected ImageButton mDelete;
    protected TimerView mEnteredTime;
    protected View mDivider;
    protected final Context mContext;
    private TextView.OnEditorActionListener mOnEditorActionListener = new EditText.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                closeSoftInputAndClearFocusFromEditText();
                return true;
            }
            return false;
        }

    };

    private void closeSoftInputAndClearFocusFromEditText() {
        InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mTimerLabel.getWindowToken(), 0);
        mTimerLabel.clearFocus();
    }

    public TimerSetupView(Context context) {
        this(context, null);
    }

    public TimerSetupView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater layoutInflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.time_setup_view, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        View v1 = findViewById(R.id.first);
        View v2 = findViewById(R.id.second);
        View v3 = findViewById(R.id.third);
        View v4 = findViewById(R.id.fourth);

        mTimerLabel = (EditText) findViewById(R.id.timer_label);
        mEnteredTime = (TimerView) findViewById(R.id.timer_time_text);
        mDelete = (ImageButton) findViewById(R.id.delete);
        mDelete.setOnClickListener(this);
        mDelete.setOnLongClickListener(this);
        mDivider = findViewById(R.id.divider);

        mNumbers[1] = (Button) v1.findViewById(R.id.key_left);
        mNumbers[2] = (Button) v1.findViewById(R.id.key_middle);
        mNumbers[3] = (Button) v1.findViewById(R.id.key_right);

        mNumbers[4] = (Button) v2.findViewById(R.id.key_left);
        mNumbers[5] = (Button) v2.findViewById(R.id.key_middle);
        mNumbers[6] = (Button) v2.findViewById(R.id.key_right);

        mNumbers[7] = (Button) v3.findViewById(R.id.key_left);
        mNumbers[8] = (Button) v3.findViewById(R.id.key_middle);
        mNumbers[9] = (Button) v3.findViewById(R.id.key_right);

        mLeft = (Button) v4.findViewById(R.id.key_left);
        mNumbers[0] = (Button) v4.findViewById(R.id.key_middle);
        mRight = (Button) v4.findViewById(R.id.key_right);

        mLeft.setVisibility(INVISIBLE);
        mRight.setVisibility(INVISIBLE);

        for (int i = 0; i < 10; i++) {
            mNumbers[i].setOnClickListener(this);
            mNumbers[i].setText(String.format("%d", i));
            mNumbers[i].setTextColor(Color.WHITE);
            mNumbers[i].setTag(R.id.numbers_key, new Integer(i));
        }
        updateTime();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            //TODO
            Typeface robotoThin = Typeface.
                    createFromAsset(mContext.getAssets(), "fonts/Roboto-Thin.ttf");
            for (Button mNumber : mNumbers) {
                mNumber.setTypeface(robotoThin);
            }
//            mEnteredTime.setTypeface(robotoThin);
        }

        mTimerLabel.setOnEditorActionListener(mOnEditorActionListener);
    }

    public void registerStartButton(ImageButton start) {
        mStart = start;
        initializeStartButtonVisibility();
    }

    private void initializeStartButtonVisibility() {
        if (mStart != null) {
            mStart.setVisibility(isInputHasValue() ? View.VISIBLE : View.INVISIBLE);
        }
    }

    private void updateStartButton() {
        setFabButtonVisibility(isInputHasValue() /* show or hide */);
    }

    public void updateDeleteButtonAndDivider() {
        final boolean enabled = isInputHasValue();
        if (mDelete != null) {
            mDelete.setEnabled(enabled);
            mDivider.setBackgroundResource(enabled ? R.color.colorPrimary : R.color.dialog_gray);
        }
    }

    private boolean isInputHasValue() {
        return mInputPointer != -1;
    }

    public void setFabButtonVisibility(boolean show) {
        final int finalVisibility = show ? View.VISIBLE : View.INVISIBLE;
        if (mStart == null || mStart.getVisibility() == finalVisibility) {
            // Fab is not initialized yet or already shown/hidden
            return;
        }
        setupScaleAnimator(show, mStart);
    }

    @Override
    public void onClick(View v) {
        closeSoftInputAndClearFocusFromEditText();
        v.requestFocus();
        doOnClick(v);
        updateStartButton();
        updateDeleteButtonAndDivider();
    }

    protected void doOnClick(View v) {

        Integer val = (Integer) v.getTag(R.id.numbers_key);
        // A number was pressed
        if (val != null) {
            // pressing "0" as the first digit does nothing
            if (mInputPointer == -1 && val == 0) {
                return;
            }
            if (mInputPointer < mInputSize - 1) {
                for (int i = mInputPointer; i >= 0; i--) {
                    mInput[i + 1] = mInput[i];
                }
                mInputPointer++;
                mInput[0] = val;
                updateTime();
            }
            return;
        }

        // other keys
        if (v == mDelete) {
            if (mInputPointer >= 0) {
                for (int i = 0; i < mInputPointer; i++) {
                    mInput[i] = mInput[i + 1];
                }
                mInput[mInputPointer] = 0;
                mInputPointer--;
                updateTime();
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (v == mDelete) {
            reset();
            updateDeleteButtonAndDivider();
            return true;
        }
        return false;
    }

    protected void updateTime() {
        mEnteredTime.setTime(mInput[4], mInput[3], mInput[2],
                mInput[1] * 10 + mInput[0]);
    }

    public void reset() {
        for (int i = 0; i < mInputSize; i++) {
            mInput[i] = 0;
        }
        mInputPointer = -1;
        updateTime();
        updateStartButton();
    }

    public long getTime() {
        return (mInput[4] * 3600 + mInput[3] * 600 + mInput[2] * 60 + mInput[1] * 10 + mInput[0]) * DateUtils.SECOND_IN_MILLIS;
    }

    public String getTimerLabel() {
        return mTimerLabel.getText().toString();
    }

    public void setTimerLabel(String timerLabel) {
        mTimerLabel.setText(timerLabel);
    }

    public void saveEntryState(Bundle outState, String key) {
        outState.putIntArray(key, mInput);
    }

    public void restoreEntryState(Bundle inState, String key) {
        int[] input = inState.getIntArray(key);
        if (input != null && mInputSize == input.length) {
            for (int i = 0; i < mInputSize; i++) {
                mInput[i] = input[i];
                if (mInput[i] != 0) {
                    mInputPointer = i;
                }
            }
            updateTime();
        }
        initializeStartButtonVisibility();
    }

    public void prepareToShow() {
        for (Button mNumber : mNumbers) {
            mNumber.setVisibility(View.INVISIBLE);
        }
        mTimerLabel.setVisibility(View.INVISIBLE);
        mDelete.setVisibility(View.INVISIBLE);
        mEnteredTime.setVisibility(View.INVISIBLE);
        mDivider.setVisibility(View.INVISIBLE);
    }

    public void show() {
        scaleAnimateUi(true);
    }

    public void hide() {
        scaleAnimateUi(false);
    }

    private void scaleAnimateUi(boolean show) {
        final int finalVisibility = show ? View.VISIBLE : View.INVISIBLE;
        if (mNumbers[0] == null || mNumbers[0].getVisibility() == finalVisibility) {
            // Fab is not initialized yet or already shown/hidden
            return;
        }

        for (Button mNumber : mNumbers) {
            setupScaleAnimator(show, mNumber);
        }
        setupScaleAnimator(show, mTimerLabel);
        setupScaleAnimator(show, mDelete);
        setupScaleAnimator(show, mEnteredTime);
        setupScaleAnimator(show, mDivider);
    }

    private void setupScaleAnimator(boolean show, View target) {
        final Animator scaleAnimator = AnimatorUtils.getScaleAnimator(
                target, show ? 0.0f : 1.0f, show ? 1.0f : 0.0f);
        scaleAnimator.setDuration(AnimatorUtils.ANIM_DURATION_SHORT);
        if (show) {
            scaleAnimator.addListener(new ScaleShowListenerAdapter(target));
        } else {
            scaleAnimator.addListener(new ScaleHideListenerAdapter(target));
        }
        scaleAnimator.start();
    }

    class ScaleHideListenerAdapter extends AnimatorListenerAdapter {
        private View target;

        public ScaleHideListenerAdapter(View target) {

            this.target = target;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (target != null) {
                target.setScaleX(1.0f);
                target.setScaleY(1.0f);
                target.setVisibility(View.INVISIBLE);
            }
        }
    }

    class ScaleShowListenerAdapter extends AnimatorListenerAdapter {
        private View target;

        public ScaleShowListenerAdapter(View target) {

            this.target = target;
        }

        @Override
        public void onAnimationStart(Animator animation) {
            if (target != null) {
                target.setVisibility(View.VISIBLE);
            }
        }
    }
}
