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

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.transition.Explode;
import android.transition.Transition;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;

import com.leinardi.kitchentimer.R;
import com.leinardi.kitchentimer.ui.transition.RevealTransition;
import com.leinardi.kitchentimer.ui.widget.PlayPauseDrawable;
import com.leinardi.kitchentimer.ui.widget.TimerSetupView;
import com.leinardi.kitchentimer.utils.AnimatorUtils;
import com.melnykov.fab.FloatingActionButton;

public class TimerSetupActivity extends BaseActivity {
    public static final String EXTRA_EPICENTER = "EXTRA_EPICENTER";
    public static final String EXTRA_ORIENTATION = "EXTRA_ORIENTATION";
    public static final String EXTRA_TIME = "EXTRA_TIME";
    public static final String EXTRA_LABEL = "EXTRA_LABEL";
    private static final String KEY_TIMER_SETUP_VIEW = "TIMER_VIEW_SETUP";

    private TimerSetupView mTimerSetupView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_setup);

        mTimerSetupView = (TimerSetupView) findViewById(R.id.timer_setup);
        FloatingActionButton fabPlay = (FloatingActionButton) findViewById(R.id.fab_play);

        PlayPauseDrawable playPauseDrawable = new PlayPauseDrawable(TimerSetupActivity.this);
        playPauseDrawable.setPausePlay(true);
        fabPlay.setImageDrawable(playPauseDrawable);

        mTimerSetupView.registerStartButton(fabPlay);
        fabPlay.setOnClickListener(mFabPlayClickListener);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            initTransitions();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mTimerSetupView.saveEntryState(outState, KEY_TIMER_SETUP_VIEW);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mTimerSetupView.restoreEntryState(savedInstanceState, KEY_TIMER_SETUP_VIEW);
    }

    private View.OnClickListener mFabPlayClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra(EXTRA_TIME, mTimerSetupView.getTime());
            returnIntent.putExtra(EXTRA_LABEL, mTimerSetupView.getTimerLabel());
            setResult(RESULT_OK, returnIntent);
            ActivityCompat.finishAfterTransition(TimerSetupActivity.this);
        }
    };

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initTransitions() {
        Window window = getWindow();
        int originalOrientation = getIntent().getExtras().getInt(EXTRA_ORIENTATION);
        if (getResources().getConfiguration().orientation == originalOrientation) {
            setupEnterTransition(window);

            setupReturnTransition(window);
        } else {
            window.setReturnTransition(new Explode());
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupEnterTransition(Window window) {
        RevealTransition revealEnter = createRevealTransition();
        revealEnter.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                mTimerSetupView.prepareToShow();
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                mTimerSetupView.show();

                // Make sure we remove ourselves as a listener
                transition.removeListener(this);
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });
        window.setEnterTransition(revealEnter);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupReturnTransition(Window window) {
        RevealTransition revealReturn = createRevealTransition();
        revealReturn.setStartDelay(AnimatorUtils.ANIM_DURATION_SHORT);
        revealReturn.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                mTimerSetupView.hide();
                mTimerSetupView.setFabButtonVisibility(false);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                // Make sure we remove ourselves as a listener
                transition.removeListener(this);
            }

            @Override
            public void onTransitionCancel(Transition transition) {
            }

            @Override
            public void onTransitionPause(Transition transition) {
            }

            @Override
            public void onTransitionResume(Transition transition) {
            }
        });
        window.setReturnTransition(revealReturn);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private RevealTransition createRevealTransition() {
        Point epicenter = getIntent().getParcelableExtra(EXTRA_EPICENTER);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int bigRadius = Math.max(displayMetrics.widthPixels, displayMetrics.heightPixels);
        RevealTransition reveal = new RevealTransition(epicenter, 0, bigRadius, AnimatorUtils.ANIM_DURATION_SHORT);
        reveal.addTarget(R.id.main_container);
        reveal.addTarget(android.R.id.statusBarBackground);
        return reveal;
    }

}
