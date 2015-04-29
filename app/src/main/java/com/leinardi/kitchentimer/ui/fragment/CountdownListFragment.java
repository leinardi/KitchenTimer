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

import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.leinardi.kitchentimer.R;
import com.leinardi.kitchentimer.model.Countdown;
import com.leinardi.kitchentimer.ui.InsetDecoration;
import com.leinardi.kitchentimer.ui.MainActivity;
import com.leinardi.kitchentimer.ui.TimerSetupActivity;
import com.leinardi.kitchentimer.ui.adapter.CountdownAdapter;
import com.leinardi.kitchentimer.ui.widget.EmptyRecyclerView;
import com.leinardi.kitchentimer.utils.AnimatorUtils;
import com.leinardi.kitchentimer.utils.LogUtils;
import com.melnykov.fab.FloatingActionButton;

import io.realm.Realm;
import jp.wasabeef.recyclerview.animators.ScaleInAnimator;


public class CountdownListFragment extends Fragment {
    private static final String TAG = CountdownListFragment.class.getSimpleName();
    private EmptyRecyclerView mRecyclerView;
    private CountdownAdapter mCountdownAdapter;
    private FloatingActionButton mFabAdd;
    private boolean mTicking = false;

    private Realm mRealm;
    private final Runnable mClockTick = new Runnable() {
        final static int TIME_DELAY_MS = 500;

        @Override
        public void run() {
            mRealm.beginTransaction();
            for (int i = 0; i < mCountdownAdapter.getItemCount(); i++) {
                final Countdown countdown = mCountdownAdapter.getItem(i);
                if (countdown.getState() == Countdown.STATE_RUNNING || countdown.getState() == Countdown.STATE_TIMESUP) {
                    Countdown.updateTimeLeft(countdown, false);
                }
                if (countdown.getTimeLeft() <= 0 && countdown.getState() == Countdown.STATE_RUNNING) {
                    countdown.setState(Countdown.STATE_TIMESUP);
                }
            }
            mRealm.commitTransaction();
            mRecyclerView.postDelayed(mClockTick, TIME_DELAY_MS);
        }
    };

    public CountdownListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = Countdown.getRealmInstance(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_countdown_list, container, false);

        initUi(rootView);

        return rootView;
    }

    private void initUi(final View rootView) {
        mRecyclerView = (EmptyRecyclerView) rootView.findViewById(R.id.recycler_view);
        AppCompatTextView emptiView = (AppCompatTextView) rootView.findViewById(R.id.empty_view);
        Typeface robotoSlab = Typeface.
                createFromAsset(getActivity().getAssets(), "fonts/RobotoSlab-Regular.ttf");
        emptiView.setTypeface(robotoSlab);
        mRecyclerView.setEmptyView(emptiView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(new InsetDecoration(getActivity()));

        mRecyclerView.setItemAnimator(new ScaleInAnimator());
        mRecyclerView.getItemAnimator().setAddDuration(AnimatorUtils.ANIM_DURATION_SHORT);
        mRecyclerView.getItemAnimator().setChangeDuration(AnimatorUtils.ANIM_DURATION_SHORT);
        mRecyclerView.getItemAnimator().setMoveDuration(AnimatorUtils.ANIM_DURATION_SHORT);
        mRecyclerView.getItemAnimator().setRemoveDuration(AnimatorUtils.ANIM_DURATION_SHORT);

        mCountdownAdapter = new CountdownAdapter(getActivity());

        mRecyclerView.setAdapter(mCountdownAdapter);

        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                findAndUseBestFitColumnNumber();
            }

            private void findAndUseBestFitColumnNumber() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    rootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                float recyclerViewWidth = rootView.getMeasuredWidth();
                float countdownCardMinimumWidth = getResources().getDimension(R.dimen.countdown_card_minimum_width);

                int columnNumber = (int) Math.floor(recyclerViewWidth / countdownCardMinimumWidth);

                if (columnNumber > 0) {
                    mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), columnNumber, GridLayoutManager.VERTICAL, false));
                }
            }
        });

        mFabAdd = (FloatingActionButton) rootView.findViewById(R.id.fab_add);
//        mFabAdd.attachToRecyclerView(mRecyclerView);

        mFabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTimerSetupActivity();
            }
        });
    }

    public void fabMoveDown(float value) {
        mFabAdd.animate().translationY(value);
    }

    private void startTimerSetupActivity() {
        Intent intent = new Intent(getActivity(), TimerSetupActivity.class);

        ViewCompat.setTransitionName(mFabAdd, "FabAdd");

        int[] locations = new int[2];
        mFabAdd.getLocationOnScreen(locations);
        int x = locations[0];
        int y = locations[1];

        Point epicenter = new Point(x + mFabAdd.getWidth() / 2,
                y);
        intent.putExtra(TimerSetupActivity.EXTRA_EPICENTER, epicenter);
        int orientation = getResources().getConfiguration().orientation;
        intent.putExtra(TimerSetupActivity.EXTRA_ORIENTATION, orientation);

        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                        mFabAdd, ViewCompat.getTransitionName(mFabAdd));
        ActivityCompat.startActivityForResult(getActivity(), intent,
                MainActivity.REQUEST_CODE_COUNTDOWN_DURATION, options.toBundle());
    }

    public void addCountdownTimer(Countdown countdown) {
        mCountdownAdapter.addItem(countdown);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mCountdownAdapter != null) {
            mCountdownAdapter.onDestroy();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.d(TAG, "onResume");
        startClockTicks();
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtils.d(TAG, "onPause");
        stopClockTicks();
    }

    // Starts the ticks that animate the timers.
    private void startClockTicks() {
        mRecyclerView.postDelayed(mClockTick, 20);
        mTicking = true;
    }

    // Stops the ticks that animate the timers.
    private void stopClockTicks() {
        if (mTicking) {
            mRecyclerView.removeCallbacks(mClockTick);
            mTicking = false;
        }
    }

    public void notifyDatasetChanged() {
        if (mCountdownAdapter != null) {
            mCountdownAdapter.notifyDataSetChanged();
        }
    }
}