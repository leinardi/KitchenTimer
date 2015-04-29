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

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.leinardi.kitchentimer.R;

/**
 * Created by leinardi on 13/04/15.
 */
public class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {
    private int mInsets;

    public SimpleDividerItemDecoration(Context context) {
        mInsets = context.getResources().getDimensionPixelSize(R.dimen.divider_insets);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = 0;
        outRect.right = 0;
        outRect.bottom = 0;

        if (parent.getChildAdapterPosition(view) != 0){
            outRect.top = mInsets;
        }
    }
}