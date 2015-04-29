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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;

import com.daimajia.swipe.SwipeLayout;
import com.leinardi.kitchentimer.R;
import com.leinardi.kitchentimer.model.Preset;
import com.leinardi.kitchentimer.utils.Utils;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by leinardi on 13/04/15.
 */
public class PresetAdapter extends RecyclerView.Adapter<PresetAdapter.VerticalItemHolder> {
    private static final String TAG = PresetAdapter.class.getSimpleName();

    private final RealmResults<Preset> mRealmResults;
    private final Realm mRealm;

    private AdapterView.OnItemClickListener mOnItemClickListener;

    public PresetAdapter(Context context) {
        this.mRealm = Preset.getRealmInstance(context);
        this.mRealmResults = mRealm.where(Preset.class).findAll();
    }

    public static class VerticalItemHolder extends RecyclerView.ViewHolder {
        SwipeLayout swipeLayout;
        AppCompatTextView label, duration;
        ImageButton delete;


        public VerticalItemHolder(final View itemView, final PresetAdapter adapter) {
            super(itemView);

            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swype_layout);
            label = (AppCompatTextView) itemView.findViewById(R.id.tv_label);
            duration = (AppCompatTextView) itemView.findViewById(R.id.tv_duration);
            delete = (ImageButton) itemView.findViewById(R.id.ib_delete);

            swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
                @Override
                public void onStartOpen(SwipeLayout layout) {
                }

                @Override
                public void onOpen(SwipeLayout layout) {
                    int adapterPosition = getAdapterPosition();
                    if (adapterPosition >= 0 && adapterPosition < adapter.getItemCount()) {
                        Realm realm = Preset.getRealmInstance(itemView.getContext());
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
                        Realm realm = Preset.getRealmInstance(itemView.getContext());
                        realm.beginTransaction();
                        adapter.getItem(getAdapterPosition()).setIsSwipeLayoutOpen(false);
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
                    if (adapter.getItem(getAdapterPosition()).isSwipeLayoutOpen()) {
                        v.open(false, false);
                    } else {
                        v.close(false, false);
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
    public void addItem(Preset preset) {
        mRealm.beginTransaction();
        mRealm.copyToRealm(preset);
        mRealm.commitTransaction();

        notifyItemInserted(getItemCount());
    }

    public Preset getItem(int position) {
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

    public boolean removeItem(Preset preset) {
        for (int position = 0; position < mRealmResults.size(); position++) {
            if (mRealmResults.get(position).getId().equals(preset.getId())) {
                removeItem(position);
                return true;
            }
        }
        return false;
    }

    @Override
    public VerticalItemHolder onCreateViewHolder(ViewGroup container, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        View root = inflater.inflate(R.layout.item_preset, container, false);

        return new VerticalItemHolder(root, this);
    }

    @Override
    public void onBindViewHolder(final VerticalItemHolder itemHolder, final int position) {
        Preset item = mRealmResults.get(position);

        itemHolder.label.setText(item.getLabel());
        itemHolder.duration.setText(Utils.getHumanReadableDuration(item.getDuration()));

        itemHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeItem(position);
            }
        });

        itemHolder.swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemHolderClick(itemHolder);
            }
        });

        itemHolder.swipeLayout.getSurfaceView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                itemHolder.swipeLayout.open();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRealmResults.size();
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    private void onItemHolderClick(VerticalItemHolder itemHolder) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(null, itemHolder.itemView,
                    itemHolder.getAdapterPosition(), itemHolder.getItemId());
        }
    }

    public void onDestroy() {
        mRealm.close();
    }
}
