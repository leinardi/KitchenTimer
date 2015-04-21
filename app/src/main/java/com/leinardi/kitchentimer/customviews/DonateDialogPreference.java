/**
 * Kitchen Timer
 * Copyright (C) 2010 Roberto Leinardi
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.leinardi.kitchentimer.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;

import com.leinardi.kitchentimer.R;
import com.leinardi.kitchentimer.utils.Utils;

public class DonateDialogPreference extends DialogPreference {
    Context mContext;
    String fileName;

    public DonateDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.MyDialogPreference);
        fileName = a.getString(R.styleable.MyDialogPreference_fileName);
    }

//	protected void onPrepareDialogBuilder(Builder builder) {
//		builder.setView(Utils.dialogWebView(mContext, fileName));
//	}

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult) {
            Utils.donate(mContext);
        }
    }
}
