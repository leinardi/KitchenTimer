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

package com.leinardi.kitchentimer.ui.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;

import com.daimajia.swipe.SwipeLayout;
import com.leinardi.kitchentimer.R;
import com.leinardi.kitchentimer.model.Countdown;
import com.leinardi.kitchentimer.receiver.TimerReceiver;
import com.leinardi.kitchentimer.ui.widget.CountdownView;
import com.leinardi.kitchentimer.ui.widget.PlayPauseView;
import com.leinardi.kitchentimer.utils.Utils;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by leinardi on 13/04/15.
 */
public class CountdownAdapter extends RecyclerView.Adapter<CountdownAdapter.ViewHolder> {
    private static final String TAG = CountdownAdapter.class.getSimpleName();

    private final Context mContext;
    private final Realm mRealm;
    private final RealmResults<Countdown> mRealmResults;

    private AdapterView.OnItemClickListener mOnItemClickListener;

    public CountdownAdapter(Context context) {
        this.mContext = context;
        this.mRealm = Countdown.getRealmInstance(context);
        this.mRealmResults = mRealm.where(Countdown.class).findAll();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        SwipeLayout swipeLayout;
        CardView cardView;
        AppCompatTextView label;
        CountdownView countdownView;
        PlayPauseView playPause;
        ImageButton delete;

        public ViewHolder(final View itemView, final CountdownAdapter adapter) {
            super(itemView);

            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swype_layout);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            label = (AppCompatTextView) itemView.findViewById(R.id.tv_label);
            countdownView = (CountdownView) itemView.findViewById(R.id.countdown);
            playPause = (PlayPauseView) itemView.findViewById(R.id.play_pause_view);
            delete = (ImageButton) itemView.findViewById(R.id.ib_delete);

            swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
                @Override
                public void onStartOpen(SwipeLayout layout) {
                }

                @Override
                public void onOpen(SwipeLayout layout) {
                    int adapterPosition = getAdapterPosition();
                    if (adapterPosition >= 0 && adapterPosition < adapter.getItemCount()) {
                        Realm realm = Countdown.getRealmInstance(itemView.getContext());
                        realm.beginTransaction();
                        adapter.getItem(getAdapterPosition()).setIsSwipeLayoutOpen(true);
                        realm.commitTransaction();
                        realm.close();
                    }
                }

                @Override
                public void onStartClose(SwipeLayout layout) {
                }

                @Override
                public void onClose(SwipeLayout layout) {
                    int adapterPosition = getAdapterPosition();
                    if (adapterPosition >= 0 && adapterPosition < adapter.getItemCount()) {
                        Realm realm = Countdown.getRealmInstance(itemView.getContext());
                        realm.beginTransaction();
                        adapter.getItem(adapterPosition).setIsSwipeLayoutOpen(false);
                        realm.commitTransaction();
                        realm.close();
                    }
                }

                @Override
                public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                }

                @Override
                public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                }
            });

            swipeLayout.addOnLayoutListener(new SwipeLayout.OnLayout() {
                @Override
                public void onLayout(SwipeLayout v) {
                    int position = getAdapterPosition();
//                    LogUtils.d(TAG, "onLayout " + position + " " + v.getOpenStatus());

                    if (position != -1) {
                        if (adapter.getItem(position).isSwipeLayoutOpen()) {
                            v.open(false, false);
                        } else {
                            v.close(false, false);
                        }
                    }
                }
            });

        }
    }


    /*
     * Inserting a new item in the list. This uses a specialized
     * RecyclerView method, notifyItemInserted(), to trigger any enabled item
     * animations in addition to updating the view.
     */
    public void addItem(Countdown countdown) {
        mRealm.beginTransaction();
        Countdown countdownRealm = mRealm.copyToRealm(countdown);
        mRealm.commitTransaction();
        notifyItemInserted(getItemCount());
        TimerReceiver.updateTimerState(mContext, countdownRealm, TimerReceiver.START_TIMER);
    }

    public Countdown getItem(int position) {
        return mRealmResults.get(position);
    }


    /*
     * Inserting a new item at the head of the list. This uses a specialized
     * RecyclerView method, notifyItemRemoved(), to trigger any enabled item
     * animations in addition to updating the view.
     */
    public void removeItem(int position) {
        if (position >= mRealmResults.size()) return;
        mRealm.beginTransaction();
        mRealmResults.remove(position);
        mRealm.commitTransaction();
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mRealmResults.size());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup container, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        View root = inflater.inflate(R.layout.item_countdown, container, false);

        return new ViewHolder(root, this);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        final Countdown countdown = getItem(position);

        viewHolder.swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (countdown.getState() == Countdown.STATE_TIMESUP
                        && viewHolder.swipeLayout.getOpenStatus() == SwipeLayout.Status.Close) {
                    deleteCountdown(position, viewHolder);
                } else {
                    viewHolder.swipeLayout.toggle();
                }
                onItemHolderClick(viewHolder);
            }
        });

        viewHolder.label.setText(countdown.getLabel());

        viewHolder.countdownView.setCountdown(countdown);

        viewHolder.playPause.setPausePlay(!Countdown.isTicking(countdown));

        viewHolder.playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (countdown.getTimeLeft() < 0) {
                    deleteCountdown(position, viewHolder);
                } else {
                    PlayPauseView playPauseView = (PlayPauseView) view;
                    if (countdown.getState() == Countdown.STATE_RUNNING) {
                        pauseCountdown(countdown);
                    } else {
                        startCountdown(countdown);
                    }
                    // TODO hadle fast taps bug
                    playPauseView.toggle();
                }
            }
        });

        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCountdown(position, viewHolder);
            }
        });

    }

    private void pauseCountdown(Countdown countdown) {
        mRealm.beginTransaction();
        countdown.setState(Countdown.STATE_STOPPED);
        mRealm.commitTransaction();
        TimerReceiver.updateTimerState(mContext, countdown, TimerReceiver.TIMER_STOP);

    }

    private void startCountdown(Countdown countdown) {
        mRealm.beginTransaction();
        countdown.setState(Countdown.STATE_RUNNING);
        countdown.setStartTime(Utils.getTimeNow() - (countdown.getOriginalLength() - countdown.getTimeLeft()));
        mRealm.commitTransaction();
        TimerReceiver.updateTimerState(mContext, countdown, TimerReceiver.START_TIMER);
    }

    private void deleteCountdown(int position, ViewHolder viewHolder) {
        if (position >= 0 && position < getItemCount()) {
            viewHolder.swipeLayout.close();
            TimerReceiver.updateTimerState(mContext, getItem(position), TimerReceiver.DELETE_TIMER);
            removeItem(position);
        }
    }

    @Override
    public int getItemCount() {
        return mRealmResults.size();
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    private void onItemHolderClick(ViewHolder viewHolder) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(null, viewHolder.itemView,
                    viewHolder.getAdapterPosition(), viewHolder.getItemId());
        }
    }

    public void onDestroy() {
        mRealm.close();
    }
}
