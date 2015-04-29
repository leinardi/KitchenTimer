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


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.leinardi.kitchentimer.R;
import com.leinardi.kitchentimer.model.Preset;
import com.leinardi.kitchentimer.ui.MainActivity;
import com.leinardi.kitchentimer.ui.SimpleDividerItemDecoration;
import com.leinardi.kitchentimer.ui.adapter.PresetAdapter;
import com.leinardi.kitchentimer.ui.widget.EmptyRecyclerView;
import com.leinardi.kitchentimer.utils.AnimatorUtils;

import jp.wasabeef.recyclerview.animators.OvershootInLeftAnimator;

public class PresetListFragment extends Fragment {
    private PresetAdapter mPresetAdapter;

    public PresetListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_preset_list, container, false);

        initUi(rootView);

        return rootView;
    }

    private void initUi(View rootView) {
        EmptyRecyclerView mRecyclerView = (EmptyRecyclerView) rootView.findViewById(R.id.recycler_view);
        AppCompatTextView emptiView = (AppCompatTextView) rootView.findViewById(R.id.empty_view);
        Typeface robotoSlab = Typeface.
                createFromAsset(getActivity().getAssets(), "fonts/RobotoSlab-Regular.ttf");
        emptiView.setTypeface(robotoSlab);
        mRecyclerView.setEmptyView(emptiView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));

        mRecyclerView.setItemAnimator(new OvershootInLeftAnimator());
        mRecyclerView.getItemAnimator().setAddDuration(AnimatorUtils.ANIM_DURATION_SHORT);
        mRecyclerView.getItemAnimator().setChangeDuration(AnimatorUtils.ANIM_DURATION_SHORT);
        mRecyclerView.getItemAnimator().setMoveDuration(AnimatorUtils.ANIM_DURATION_SHORT);
        mRecyclerView.getItemAnimator().setRemoveDuration(AnimatorUtils.ANIM_DURATION_SHORT);

        mPresetAdapter = new PresetAdapter(getActivity());


        mPresetAdapter.setOnItemClickListener(mPresetItemClickListener);
        mRecyclerView.setAdapter(mPresetAdapter);
    }

    private AdapterView.OnItemClickListener mPresetItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Preset preset = mPresetAdapter.getItem(i);
            ((MainActivity) getActivity()).createCountdown(preset.getDuration(), preset.getLabel());
        }
    };

    public void addPreset(Preset preset) {
        mPresetAdapter.addItem(preset);
    }

    public void removePreset(Preset preset) {
        mPresetAdapter.removeItem(preset);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mPresetAdapter != null) {
            mPresetAdapter.onDestroy();
        }
    }

    public void notifyDatasetChanged() {
        if (mPresetAdapter != null) {
            mPresetAdapter.notifyDataSetChanged();
        }
    }
}
